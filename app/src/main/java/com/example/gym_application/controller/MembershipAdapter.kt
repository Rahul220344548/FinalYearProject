import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.model.MembershipPlans

class MembershipAdapter(
    private val plans: List<MembershipPlans>,
    private val onSelect: (MembershipPlans) -> Unit
) : RecyclerView.Adapter<MembershipAdapter.PlanViewHolder>() {

    inner class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.planTitle)
        private val duration: TextView = view.findViewById(R.id.planDuration)
        private val price: TextView = view.findViewById(R.id.planPrice)
        private val itemLayout: View = view
        fun bind(plan: MembershipPlans) {
            title.text = plan.title
            duration.text = plan.duration
            price.text = plan.price


            when (plan.title.lowercase()) {
                "bronze plan" -> itemLayout.setBackgroundResource(R.drawable.bronze_background)
                "silver plan" -> itemLayout.setBackgroundResource(R.drawable.silver_background)
                "gold plan" -> itemLayout.setBackgroundResource(R.drawable.gold_background)
                else -> itemLayout.setBackgroundResource(R.drawable.linear_layout_border)
            }

            itemView.setOnClickListener { onSelect(plan) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membership_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(plans[position])
    }

    override fun getItemCount(): Int = plans.size
}
