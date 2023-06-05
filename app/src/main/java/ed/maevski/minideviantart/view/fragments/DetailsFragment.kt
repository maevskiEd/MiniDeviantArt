package ed.maevski.minideviantart.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ed.maevski.minideviantart.R
import ed.maevski.minideviantart.databinding.FragmentDetailsBinding
import ed.maevski.minideviantart.view.notifications.NotificationHelper
import ed.maevski.minideviantart.viewmodel.DetailsFragmentViewModel
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.*

class DetailsFragment : Fragment() {
    lateinit var picture: DeviantPicture
    private val scope = CoroutineScope(Dispatchers.IO)
    private val detailsFragmentViewModel: DetailsFragmentViewModel by viewModels()

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        picture = arguments?.get("dev") as DeviantPicture

        //Устанавливаем сердечко
        binding.detailsFabFavorites.setImageResource(
            if (picture.isInFavorites) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )

        //Устанавливаем заголовок
        binding.detailsToolbar.title = picture.title
        //Устанавливаем картинку
        Glide.with(this)
            .load(picture.url)
            .centerCrop()
            .into(binding.detailsPoster)

        //Устанавливаем описание
        binding.detailsDescription.text = picture.description

        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
        }

        binding.detailsFabFavorites.setOnClickListener {
            if (!picture.isInFavorites) {
                binding.detailsFabFavorites.setImageResource(R.drawable.ic_baseline_favorite_24)
                picture.isInFavorites = true
            } else {
                binding.detailsFabFavorites.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                picture.isInFavorites = false
            }
        }

        binding.detailsFabShare.setOnClickListener {
            //Создаем интент
            val intent = Intent()
            //Указываем action с которым он запускается
            intent.action = Intent.ACTION_SEND
            //Кладем данные о нашем фильме
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this picture: ${picture.title} \n\n ${picture.author}\n\n ${picture.description}"
            )
            //Указываем MIME тип, чтобы система знала, какое приложения предложить
            intent.type = "text/plain"
            //Запускаем наше активити
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        binding.detailsFabWatchLater.setOnClickListener {
//            NotificationHelper.createNotification(requireContext(), picture)
            NotificationHelper.createNotification2(requireContext(), picture)
//            NotificationHelper.createForBrowserNotification(requireContext(), picture)
        }
    }

    //Узнаем, было ли получено разрешение ранее
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    //Запрашиваем разрешение
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap) {
        val image_name = getLastSegmentOfUrl(picture.url)
        val image_title = image_name.split(".")[0]


        //Проверяем версию системы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Создаем объект для передачи данных
            val contentValues = ContentValues().apply {
                //Составляем информацию для файла (имя, тип, дата создания, куда сохранять и т.д.)
                put(MediaStore.Images.Media.TITLE, image_title)
                put(MediaStore.Images.Media.DISPLAY_NAME,image_title)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED,System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            }
            //Получаем ссылку на объект Content resolver, который помогает передавать информацию из приложения вовне
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
            //Открываем канал для записи на диск
            val outputStream = contentResolver.openOutputStream(uri!!)
            //Передаем нашу картинку, может сделать компрессию
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            //Закрываем поток
            outputStream?.close()
        } else {
            //То же, но для более старых версий ОС
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                image_title,
                image_title
            )
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun performAsyncLoadOfPoster() {
        //Проверяем есть ли разрешение
        if (!checkPermission()) {
            //Если нет, то запрашиваем и выходим из метода
            requestPermission()
            return
        }
        //Создаем родительский скоуп с диспатчером Main потока, так как будем взаимодействовать с UI
        MainScope().launch {
            //Включаем Прогресс-бар
            binding.progressBar.isVisible = true
            //Создаем через async, так как нам нужен результат от работы, то есть Bitmap
            val job = scope.async {
                detailsFragmentViewModel.loadWallpaper(picture.url)
            }
            //Сохраняем в галерею, как только файл загрузится
            saveToGallery(job.await())
            //Выводим снекбар с кнопкой перейти в галерею
            Snackbar.make(
                binding.root,
                R.string.downloaded_to_gallery,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.open) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.type = "image/*"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .show()

            //Отключаем Прогресс-бар
            binding.progressBar.isVisible = false
        }
    }

    fun getLastSegmentOfUrl(url: String): String {
        // удаление параметров из URL-адреса
        val urlWithoutParams = url.split("?")[0]
        // разбиение URL-адреса на отдельные сегменты
        val segments = urlWithoutParams.split("/")
        // выбор последнего сегмента
        return segments.last()
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}