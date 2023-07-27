package ed.maevski.minideviantart.view

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ed.maevski.minideviantart.App
import ed.maevski.minideviantart.R
import ed.maevski.minideviantart.databinding.ActivityMainBinding
import ed.maevski.minideviantart.receivers.ConnectionChecker
import ed.maevski.minideviantart.view.fragments.*
import ed.maevski.minideviantart.viewmodel.MainActivityViewModel
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var scopeMainActivity: CoroutineScope

    private lateinit var receiver: BroadcastReceiver


    val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiver = ConnectionChecker()
        val filters = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        registerReceiver(receiver, filters)

        if (!App.instance.isPromoShown) {
            //Получаем доступ к Remote Config
            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            //Устанавливаем настройки
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            //Вызываем метод, который получит данные с сервера и вешаем слушатель
            firebaseRemoteConfig.fetch()
                .addOnCompleteListener {
                    //Если все получилось успешно
                    if (it.isSuccessful) {
                        //активируем последний полученный конфиг с сервера
                        firebaseRemoteConfig.activate()
                        //Получаем ссылку
                        val filmLink = firebaseRemoteConfig.getString("film_link")

                        println("Ссылка на промо изображение: $filmLink")

                        //Если поле не пустое
                        if (filmLink.isNotBlank
                                ()
                        ) {
                            //Ставим флаг, что уже промо показали
                            App.instance.isPromoShown = true
                            //Включаем промо верстку
                            binding.promoViewGroup.apply {
                                //Делаем видимой
                                visibility = View.VISIBLE
                                //Анимируем появление
                                animate()
                                    .setDuration(1500)
                                    .alpha(1f)
                                    .start()
                                //Вызываем метод, который загрузит постер в ImageView
                                setLinkForPoster(filmLink)
                                //Кнопка, по нажатии на которую промо уберется (желательно сделать отдельную кнопку с крестиком)
                                watchButton.setOnClickListener {
                                    visibility = View.GONE
                                    val uri = Uri.parse(filmLink)
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(intent)
                                }

                                //Кнопка, чтобы закрыть картинку
                                closeButton.setOnClickListener { visibility = View.GONE }
                            }
                        }
                    }
                }
        }

        scopeMainActivity = CoroutineScope(Dispatchers.IO).also { scope ->
            scope.launch {
                for (element in mainActivityViewModel.flagToken) {
                    println("MainActivity ->scopeMainActivity: flagToken: $element")
                    withContext(Dispatchers.Main) {
                        binding.mainProgressBar.isVisible = element

                        if (!element) {
                            supportFragmentManager
                                .beginTransaction()
                                .add(R.id.fragment_placeholder, HomeFragment())
                                .addToBackStack(null)
                                .commit()

                            initMenu()
                        }
                    }
                }
            }
            /*            scope.launch {
                            for (element in mainActivityViewModel.errorEvent) {
                                println("MainActivity ->scopeMainActivity: errorEvent: $element")
                                withContext(Dispatchers.Main) {
                                    if (!element) {
                                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }*/
        }

        /*        mainActivityViewModel.errorEvent.observe(this) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }*/
    }

    //Ищем фрагмент по тегу, если он есть то возвращаем его, если нет, то null
    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    fun launchDetailsFragment(picture: DeviantPicture) {
        //Создаем "посылку"
        val bundle = Bundle()
        //Кладем в "посылку"
        bundle.putParcelable("dev", picture)
        //Кладем фрагмент с деталями в перменную
        val fragment = DetailsFragment()
        //Прикрепляем нашу "посылку" к фрагменту
        fragment.arguments = bundle

        //Запускаем фрагмент
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun initMenu() {
        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home -> {
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag)

                    changeFragment(fragment ?: HomeFragment(), tag)
                    true
                }

                R.id.favorites -> {
                    if (trialPeriodCheck()) {
                        val tag = "favorites"
                        val fragment = checkFragmentExistence(tag)
                        changeFragment(fragment ?: FavoritesFragment(), tag)
                    }
                    true
                }

                R.id.watch_later -> {
                    val tag = "watch_later"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: WatchLaterFragment(), tag)
                    true
                }

                R.id.selections -> {
                    if (trialPeriodCheck()) {
                        val tag = "selections"
                        val fragment = checkFragmentExistence(tag)
                        changeFragment(fragment ?: SelectionsFragment(), tag)
                    }
                    true
                }

                R.id.settings -> {
                    val tag = "settings"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: SettingsFragment(), tag)
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun trialPeriodCheck(): Boolean {
        val timeTrialPeriodInMillis =
            mainActivityViewModel.interactor.getTrialPeriodStartFromPreferences()
        if (timeTrialPeriodInMillis == 0L) {
            AlertDialog.Builder(this)
                .setTitle("Получить пробный период?")
                .setIcon(R.drawable.baseline_more_time_24)
                .setPositiveButton("Да") { _, _ ->
                    mainActivityViewModel.interactor.saveTrialPeriodStartToPreferences(
                        System.currentTimeMillis()
                    )
                    Toast.makeText(
                        this,
                        "Вам предоставлено 5 минут пробного периода",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                .setNegativeButton("Нет") { _, _ ->

                }
                .show()
            return false
        } else if (timeTrialPeriodInMillis + MINUTES_5_IN_MILLIS <= System.currentTimeMillis()) {
            Toast.makeText(this, "Доступно в Pro версии", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        private const val MINUTES_5_IN_MILLIS = 5 * 60 * 1000
    }
}