import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gym_application.BookedClassesFragment
import com.example.gym_application.GymClassesFragment

class ClassesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GymClassesFragment()
            1 -> BookedClassesFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
