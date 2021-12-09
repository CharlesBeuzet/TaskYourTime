package com.example.taskyourtime.group

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.ChoiceItemCellBinding
import com.example.taskyourtime.model.Choice
import com.example.taskyourtime.services.GroupService

private lateinit var binding: ChoiceItemCellBinding

class ChoiceAdapter(
    private val choices: MutableList<Choice>,
    private val groupService: GroupService,
    private val context: Context?,
) : RecyclerView.Adapter<ChoiceAdapter.ChoiceHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ChoiceItemCellBinding.inflate(inflater, parent, false)
        return ChoiceHolder(binding)
    }

    override fun onBindViewHolder(holder: ChoiceHolder, position: Int) {
        val c: Choice = choices[position]
        if(c.type == "NOTE"){
            holder.genericContent.text = c.name
            holder.sidebarView.background = ColorDrawable(context!!.getColor(R.color.orange))
        }

        if(c.type == "CALENDAR"){
            holder.genericContent.text = c.name
            holder.sidebarView.background = ColorDrawable(context!!.getColor(R.color.colorPrimaryDark))
        }

        if(c.type == "TODO"){
            holder.genericContent.text = c.content
            holder.sidebarView.background = ColorDrawable(context!!.getColor(R.color.honey_yellow))
        }

        holder.checkbox.setOnClickListener{
            choices[position].checked = holder.checkbox.isChecked
        }
    }

    override fun getItemCount(): Int {
        return choices.size
    }

    class ChoiceHolder(binding: ChoiceItemCellBinding): RecyclerView.ViewHolder(binding.root){
        val genericContent = binding.genericContent
        val checkbox = binding.checkboxChosen
        val sidebarView = binding.sidebarView
        val layout = binding.root
    }
}
