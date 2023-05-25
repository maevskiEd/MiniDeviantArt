package ed.maevski.minideviantart.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import ed.maevski.minideviantart.view.MainActivity
import ed.maevski.remote_module.Item
import ed.maevski.minideviantart.databinding.FragmentHomeBinding
import ed.maevski.minideviantart.view.decoration.TopSpacingItemDecoration
import ed.maevski.minideviantart.utils.AnimationHelper
import ed.maevski.minideviantart.view.rv_adapters.ArtRecyclerAdapter
import ed.maevski.minideviantart.viewmodel.HomeFragmentViewModel
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.*
import java.util.*

class HomeFragment() : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ArtRecyclerAdapter
    private lateinit var scopeHomeFragment: CoroutineScope

    private val homeFragmentViewModel: HomeFragmentViewModel by viewModels()

    private var picturesDataBase = listOf<Item>()
        //Используем backing field
        set(value) {
            //Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            adapter.items = picturesDataBase
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        println("HomeFragment: onCreateView")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        println("HomeFragment: onViewCreated")

        homeFragmentViewModel.getDeviantArts()
        homeFragmentViewModel.picturesListData =
            homeFragmentViewModel.interactor.getDeviantPicturesFromDBWithCategory()

/*        adapter = PictureRecyclerAdapter(object : PictureRecyclerAdapter.OnItemClickListener {
            override fun click(picture: DeviantPicture) {
                Toast.makeText(requireContext(), picture.title, Toast.LENGTH_SHORT).show()
                (requireActivity() as MainActivity).launchDetailsFragment(picture)
            }
        })*/

        adapter = ArtRecyclerAdapter(object : ArtRecyclerAdapter.OnItemClickListener {
            override fun click(picture: DeviantPicture) {
                Toast.makeText(requireContext(), picture.title, Toast.LENGTH_SHORT).show()
                (requireActivity() as MainActivity).launchDetailsFragment(picture)
            }
        })

        adapter.items = picturesDataBase
//        binding.mainRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        val decorator = TopSpacingItemDecoration(8)
        binding.mainRecycler.addItemDecoration(decorator)
        binding.mainRecycler.adapter = adapter

        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.homeFragmentRoot,
            requireActivity(),
            POSITION_ONE
        )

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        //Подключаем слушателя изменений введенного текста в поиска
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            //Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String): Boolean {
                val deviantPictures: List<DeviantPicture> =
                    picturesDataBase.filter { it is DeviantPicture } as List<DeviantPicture>
                //Если ввод пуст то вставляем в адаптер всю БД
                if (newText.isEmpty()) {
                    adapter.items = picturesDataBase
                    binding.mainRecycler.adapter = adapter
                    return true
                }
                //Фильтруем список на поиск подходящих сочетаний
                val result = deviantPictures.filter {
                    //Чтобы все работало правильно, нужно и запрос, и имя фильма приводить к нижнему регистру
                    it.title.toLowerCase(Locale.getDefault())
                        .contains(newText.toLowerCase(Locale.getDefault()))
                }
                //Добавляем в адаптер
                adapter.items = result
                binding.mainRecycler.adapter = adapter
                return true
            }
        })

        scopeHomeFragment = CoroutineScope(Dispatchers.IO).also { scope ->
            scope.launch {
                homeFragmentViewModel.picturesListData.collect {
                    withContext(Dispatchers.Main) {
//                        filmsAdapter.addItems(it)
                        picturesDataBase = it
                    }
                }
            }
        }

/*        homeFragmentViewModel.picturesListLiveData.observe(viewLifecycleOwner) {
            println("homeFragmentViewModel.picturesListLiveData.observe -> $it")
            picturesDataBase = it
        }*/

        initPullToRefresh()
    }

    private fun initPullToRefresh() {
        //Вешаем слушатель, чтобы вызвался pull to refresh
        binding.pullToRefresh.setOnRefreshListener {
            //Чистим адаптер(items нужно будет сделать паблик или создать для этого публичный метод)
//            adapter.items.
/*            filmsAdapter.items.clear()*/
            //Делаем новый запрос фильмов на сервер
            homeFragmentViewModel.getDeviantArts()
            //Убираем крутящееся колечко
            binding.pullToRefresh.isRefreshing = false
        }
    }

    override fun onStop() {
        super.onStop()
        scopeHomeFragment.cancel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val POSITION_ONE = 1
    }
}