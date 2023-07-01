package ed.maevski.minideviantart.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ed.maevski.minideviantart.databinding.FragmentWatchLaterBinding
import ed.maevski.minideviantart.utils.AnimationHelper
import ed.maevski.minideviantart.view.decoration.TopSpacingItemDecoration
import ed.maevski.minideviantart.view.notifications.NotificationHelper
import ed.maevski.minideviantart.view.notifications.entity.Notification
import ed.maevski.minideviantart.view.rv_adapters.ArtRecyclerAdapter
import ed.maevski.minideviantart.view.rv_adapters.WatchLaterRecyclerAdapter
import ed.maevski.minideviantart.view.swipe.notificationItemTouchHelperCallback
import ed.maevski.minideviantart.viewmodel.WatchLaterFragmentViewModel
import ed.maevski.remote_module.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchLaterFragment : Fragment() {
    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    private lateinit var watchLaterRecyclerAdapter: WatchLaterRecyclerAdapter
    private lateinit var scopeWatchLaterFragment: CoroutineScope

    private val watchLaterFragmentViewModel: WatchLaterFragmentViewModel by viewModels()

    private var notifications = listOf<Notification>()
        //Используем backing field
        set(value) {
            //Если придет такое же значение, то мы выходим из метода
//            if (field == value) return
            //Если пришло другое значение, то кладем его в переменную
            field = value
            //Обновляем RV адаптер
            watchLaterRecyclerAdapter.items = notifications
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Закидываем interactor в NotificationHelper, чтобы можно было работать с базами данных.
        //Почему так? Потому что еще не сильно разаобрался с Dagger 2 и
        //NotificationHelper - object, а в object иньекцию сделать нельзя.
        NotificationHelper.initialize(watchLaterFragmentViewModel.interactor)


        AnimationHelper.performFragmentCircularRevealAnimation(binding.watchLaterFragmentRoot, requireActivity(), 3)

        watchLaterFragmentViewModel.notifications =
            watchLaterFragmentViewModel.interactor.getNotification()

//        initRecyckler()
        watchLaterRecyclerAdapter = WatchLaterRecyclerAdapter()

        //Добавляем swipe для нотификаций для удаления через ItemTouchHelper(callback)
        val callback = notificationItemTouchHelperCallback(watchLaterRecyclerAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.mainRecycler)

        watchLaterRecyclerAdapter.items = notifications
        binding.mainRecycler.layoutManager = LinearLayoutManager(requireContext())
//        binding.mainRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        val decorator = TopSpacingItemDecoration(8)
        binding.mainRecycler.addItemDecoration(decorator)
        binding.mainRecycler.adapter = watchLaterRecyclerAdapter

        scopeWatchLaterFragment = CoroutineScope(Dispatchers.IO).also { scope ->
            scope.launch {
                watchLaterFragmentViewModel.notifications.collect {
                    withContext(Dispatchers.Main) {
                        notifications = it
                    }
                }
            }
        }

/*        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )*/
    }

    override fun onStop() {
        super.onStop()
        scopeWatchLaterFragment.cancel()
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    private fun initRecyckler() {
        binding.mainRecycler.apply {
            watchLaterRecyclerAdapter = WatchLaterRecyclerAdapter()
            //Присваиваем адаптер
            adapter = watchLaterRecyclerAdapter
            //Присвои layoutmanager
            layoutManager = LinearLayoutManager(requireContext())
            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

}

