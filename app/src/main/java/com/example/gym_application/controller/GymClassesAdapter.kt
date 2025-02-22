import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.model.ClassModel

class GymClassesAdapter(private var classList: List<ClassModel>) :
    RecyclerView.Adapter<GymClassesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val classTitle: TextView = view.findViewById(R.id.recyClassTitle)
        val classStartTime: TextView = view.findViewById(R.id.recyClassStartTime)
        val classLength : TextView = view.findViewById(R.id.recyClassLength)
        val classRestriction: TextView = view.findViewById(R.id.recyClassRestrictions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_classes_plan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gymClass = classList[position]
        holder.classTitle.text = gymClass.classTitle
        holder.classStartTime.text = "Start: ${gymClass.startTime}"

        val startMinutes = convertTimeToMinutes(gymClass.startTime)
        val endMinutes = convertTimeToMinutes(gymClass.endTime)

        val durationMinutes = endMinutes - startMinutes

        holder.classLength.text = "$durationMinutes min"

        holder.classRestriction.text = "Class Restrictions: ${gymClass.classGenderRestrictuons}"
    }

    override fun getItemCount() = classList.size

    private fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return (hours * 60) + minutes
    }


    fun updateData(newList: List<ClassModel>) {
        classList = newList
        notifyDataSetChanged()
    }
}
