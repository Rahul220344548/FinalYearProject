import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.MainActivity
import com.example.gym_application.R
import com.example.gym_application.model.ClassModel

class GymClassesAdapter(private var classList: List<ClassModel>) :
    RecyclerView.Adapter<GymClassesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val classTitle: TextView = view.findViewById(R.id.recyClassTitle)
        val classStartTime: TextView = view.findViewById(R.id.recyClassStartTime)
        val classLength : TextView = view.findViewById(R.id.recyClassLength)
        val classRestriction: TextView = view.findViewById(R.id.recyClassRestrictions)
        val remainingBookings: TextView = view.findViewById(R.id.remainBookings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_classes_plan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gymClass = classList[position]
        holder.classTitle.text = gymClass.classTitle
        holder.classStartTime.text = "Start: ${gymClass.classStartDate}"

        val startMinutes = convertTimeToMinutes(gymClass.classStartTime)
        val endMinutes = convertTimeToMinutes(gymClass.classEndTime)

        val durationMinutes = endMinutes - startMinutes
        val remainingBookings: Int = gymClass.classMaxCapacity-gymClass.classCurrentBookings

        holder.classLength.text = "$durationMinutes min"

        holder.classRestriction.text = "Class Restrictions: ${gymClass.classGenderRestrictions}"
        holder.remainingBookings.text = "Remaining bookings: ${remainingBookings}"

        // Set click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MainActivity::class.java).apply {
                putExtra("classTitle", gymClass.classTitle)
                putExtra("classStartDate", gymClass.classStartDate)
                putExtra("classStartTime", gymClass.classStartTime)
                putExtra("classEndTime", gymClass.classEndTime)
                putExtra("classGenderRestrictions", gymClass.classGenderRestrictions)
                putExtra("classCurrentBookings", gymClass.classCurrentBookings)
                putExtra("classMaxCapacity", gymClass.classMaxCapacity)
            }
            holder.itemView.context.startActivity(intent)
        }
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
