package ed.maevski.minideviantart.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ed.maevski.minideviantart.R
import ed.maevski.minideviantart.databinding.FragmentSettingsBinding
import ed.maevski.minideviantart.utils.AnimationHelper
import ed.maevski.minideviantart.viewmodel.SettingsFragmentViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private val viewModel: SettingsFragmentViewModel by lazy {
        ViewModelProvider(this)[SettingsFragmentViewModel::class.java]
    }
/*    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(SettingsFragmentViewModel::class.java)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Подключаем анимации и передаем номер позиции у кнопки в нижнем меню
        AnimationHelper.performFragmentCircularRevealAnimation(binding.settingsFragmentRoot, requireActivity(), 5)
        //Слушаем, какой у нас сейчас выбран вариант в настройках
        viewModel.categoryPropertyLifeData.observe(viewLifecycleOwner, Observer<String> {
            when(it) {
                POPULAR_CATEGORY -> binding.radioGroup.check(R.id.radio_popular)
                NEWEST_CATEGORY -> binding.radioGroup.check(R.id.radio_newest)
                DAILYDEVIATION_CATEGORY -> binding.radioGroup.check(R.id.radio_dailydeviations)
            }
        })
        //Слушатель для отправки нового состояния в настройки
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.radio_popular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radio_newest -> viewModel.putCategoryProperty(NEWEST_CATEGORY)
                R.id.radio_dailydeviations-> viewModel.putCategoryProperty(DAILYDEVIATION_CATEGORY)
            }
        }
    }

    companion object {
        private const val POPULAR_CATEGORY = "popular"
        private const val NEWEST_CATEGORY = "newest"
        private const val DAILYDEVIATION_CATEGORY = "dailydeviations"
    }
}