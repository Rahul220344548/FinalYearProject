import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.MembershipPaymentActivity
import com.example.gym_application.R
import com.example.gym_application.admin_view.AdminMembershipInfoEditorActivity
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.newModel.NewMembershipPlan

class MembershipAdapter(
    private var plansList: List<NewMembershipPlan>,
    private val onSelect: (NewMembershipPlan) -> Unit
) : RecyclerView.Adapter<MembershipAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    class ViewHolder( view : View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.planTitle)
        val duration: TextView = view.findViewById(R.id.planDuration)
        val price: TextView = view.findViewById(R.id.planPrice)
        val itemLayout: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membership_plan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val plan = plansList[position]
        holder.title.text = plan.title
        holder.duration.text = plan.duration
        holder.price.text = plan.price.toString()

        val isSelected = position == selectedPosition


        if (isSelected) {
            holder.itemLayout.setBackgroundResource(R.color.transparentOrange)
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onSelect(plan)
        }

    }

    override fun getItemCount(): Int = plansList.size

    fun updateData( newList : List<NewMembershipPlan>) {
        plansList = newList
        notifyDataSetChanged()
    }

}
