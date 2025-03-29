import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.ClassDetailViewActivity
import com.example.gym_application.R
import com.example.gym_application.model.ClassWithScheduleModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class GymClassesAdapter(private var classList: List<ClassWithScheduleModel>) :
    RecyclerView.Adapter<GymClassesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val classTitle: TextView = view.findViewById(R.id.recyClassTitle)
        val classStartTime: TextView = view.findViewById(R.id.recyClassStartTime)
        val classLength : TextView = view.findViewById(R.id.recyClassLength)
        val classAvailabilityFor: TextView = view.findViewById(R.id.recyclassAvailabilityFor)
        val remainingBookings: TextView = view.findViewById(R.id.remainBookings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_classes_plan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gymClass = classList[position]
        holder.classTitle.text = gymClass.classLocation
        holder.classTitle.text = gymClass.classTitle
        holder.classStartTime.text = "${gymClass.classStartTime} - ${gymClass.classEndTime}"

        val startMinutes = convertTimeToMinutes(gymClass.classStartTime)
        val endMinutes = convertTimeToMinutes(gymClass.classEndTime)

        val durationMinutes = endMinutes - startMinutes
        val remainingBookings: Int = gymClass.classMaxCapacity-gymClass.classCurrentBookings

        holder.classLength.text = "$durationMinutes min"

        holder.classAvailabilityFor.text = "Class Eligible For: ${gymClass.classAvailabilityFor}"
        holder.remainingBookings.text = "available spaces: ${remainingBookings}"

        // Set click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ClassDetailViewActivity::class.java).apply {
                putExtra("scheduleId",gymClass.scheduleId)
                putExtra("classId", gymClass.classId)
                putExtra("classTitle", gymClass.classTitle)
                putExtra("classStartDate", gymClass.classStartDate)
                putExtra("classStartTime", gymClass.classStartTime)
                putExtra("classEndTime", gymClass.classEndTime)
                putExtra("classLocation", gymClass.classLocation)
                putExtra("classAvailabilityFor", gymClass.classAvailabilityFor)
                putExtra("classInstructor",gymClass.classInstructor)
                putExtra("classCurrentBookings", gymClass.classCurrentBookings)
                putExtra("classMaxCapacity", gymClass.classMaxCapacity)
                putExtra("classDescription", gymClass.classDescription)
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData(newList: List<ClassWithScheduleModel>) {
        val now = java.time.LocalTime.now()
        val today = java.time.LocalDate.now()

        val filteredList = newList.filter { classItem ->
            val classDate = LocalDate.parse(classItem.classStartDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            if (classDate.isAfter(today)) {
                // displays future classes
                true
            } else if (classDate.isEqual(today)) {
                val classTime = LocalTime.parse(classItem.classStartTime, DateTimeFormatter.ofPattern("HH:mm"))
                classTime.isAfter(now)
            } else {
                // filters out past classes
                false
            }
        }.sortedBy { convertTimeToMinutes(it.classStartTime) }

        classList = filteredList
        notifyDataSetChanged()
    }


}
