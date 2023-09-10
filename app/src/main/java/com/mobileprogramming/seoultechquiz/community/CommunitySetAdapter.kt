package com.mobileprogramming.seoultechquiz.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mobileprogramming.seoultechquiz.R

class CommunitySetAdapter(context: Context) : BaseAdapter() {
    private val communitySets: MutableList<CommunitySet> = mutableListOf()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return communitySets.size
    }

    override fun getItem(position: Int): CommunitySet {
        return communitySets[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.quiz_set_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val quizSet = getItem(position)
        viewHolder.bind(quizSet)

        return view
    }

    fun setQuizSets(quizSets: MutableList<CommunitySet>) {
        this.communitySets.clear()
        this.communitySets.addAll(quizSets)
        notifyDataSetChanged()
    }

    fun addQuizSet(quizSet: CommunitySet) {
        communitySets.add(quizSet)
        notifyDataSetChanged()
    }

    fun updateQuizSet(position: Int, updatedQuizSet: CommunitySet) {
        communitySets[position] = updatedQuizSet
        notifyDataSetChanged()
    }

    fun removeQuizSet(position: Int) {
        communitySets.removeAt(position)
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        private val textViewQuizSetName: TextView = view.findViewById(R.id.textViewQuizSetName)
        private val textViewWord: TextView = view.findViewById(R.id.textViewWord)
        private val textViewDefinition: TextView = view.findViewById(R.id.textViewDefinition)

        fun bind(communitySet: CommunitySet) {
            textViewQuizSetName.text = communitySet.quizSetName
            textViewWord.text = "Word: ${communitySet.word}"
            textViewDefinition.text = "Definition: ${communitySet.definition}"
        }
    }
}