package com.mobileprogramming.seoultechquiz.quiz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mobileprogramming.seoultechquiz.R

class QuizSetAdapter(context: Context) : BaseAdapter() {
    private val quizSets: MutableList<QuizSet> = mutableListOf()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return quizSets.size
    }

    override fun getItem(position: Int): QuizSet {
        return quizSets[position]
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

    fun setQuizSets(quizSets: List<QuizSet>) {
        this.quizSets.clear()
        this.quizSets.addAll(quizSets)
        notifyDataSetChanged()
    }

    fun addQuizSet(quizSet: QuizSet) {
        quizSets.add(quizSet)
        notifyDataSetChanged()
    }

    fun updateQuizSet(position: Int, updatedQuizSet: QuizSet) {
        quizSets[position] = updatedQuizSet
        notifyDataSetChanged()
    }

    fun removeQuizSet(position: Int) {
        quizSets.removeAt(position)
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        private val textViewQuizSetName: TextView = view.findViewById(R.id.textViewQuizSetName)
        private val textViewWord: TextView = view.findViewById(R.id.textViewWord)
        private val textViewDefinition: TextView = view.findViewById(R.id.textViewDefinition)

        fun bind(quizSet: QuizSet) {
            textViewQuizSetName.text = quizSet.quizSetName
            textViewWord.text = "Word: ${quizSet.word}"
            textViewDefinition.text = "Definition: ${quizSet.definition}"
        }
    }
}