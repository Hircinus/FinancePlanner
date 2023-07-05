package com.example.finance

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.finance.db.DataManager
import com.example.finance.db.MonthManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val dm = DataManager(requireActivity())
        val mm = MonthManager(requireActivity())
        val currentTime = System.currentTimeMillis()
        var nextID = 0;
        var obj = mm.getLast()
        if (obj.moveToLast()) {
            nextID = obj.getInt(0)
        }
        obj.close()
        //dm.insert("Budget1",100.0,"personal", nextID)
        val historyHolder: LinearLayout = view.findViewById<LinearLayout>(R.id.historyHolder)
        obj = dm.selectAllByCategory()
        var periodMemory = -1
        var categoryMemory = ""
        var periodFlag = false
        var categoryFlag = false
        var totalBudget = 0.0
        var totalBalance = 0.0
        while(obj.moveToNext()) {
            if(periodMemory == -1 || obj.getInt(5) != periodMemory) {
                periodMemory = obj.getInt(5)
                periodFlag = true
            }
            if(categoryMemory == "" || obj.getString(2).lowercase() != categoryMemory) {
                categoryMemory = obj.getString(2).lowercase()
                categoryFlag = true
            }
            val currentMonth = mm.searchID(obj.getInt(5))
            if(currentMonth.moveToLast()) {
                if(periodFlag) {
                    val calendar: Calendar = Calendar.getInstance()
                    val formatter = SimpleDateFormat("MMMM d, yyyy")
                    calendar.timeInMillis = currentMonth.getLong(1)
                    val startTime = formatter.format(calendar.time)
                    val newCalendar: Calendar = Calendar.getInstance()
                    val newFormatter = SimpleDateFormat("MMMM d, yyyy")
                    newCalendar.timeInMillis = currentMonth.getLong(2)
                    val endTime = newFormatter.format(newCalendar.time)
                    val periodHolder = TextView(activity)
                    periodHolder.text = "Period: $startTime to $endTime"
                    var styleResId = R.style.budget_center_layout
                    var styledTextView = ContextThemeWrapper(requireContext(), styleResId)
                    periodHolder.setTextAppearance(styledTextView, styleResId)
                    periodHolder.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    historyHolder.addView(periodHolder)
                }
                if(categoryFlag) {
                    val categoryHolder = TextView(activity)
                    categoryHolder.text = "Category: ${obj.getString(2)}"
                    var styleResId = R.style.budget_name_layout
                    var styledTextView = ContextThemeWrapper(requireContext(), styleResId)
                    categoryHolder.setTextAppearance(styledTextView, styleResId)
                    categoryHolder.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    historyHolder.addView(categoryHolder)
                }
                totalBudget += obj.getDouble(3)
                totalBalance += obj.getDouble(4)
                val budgetNameHolder = TextView(activity)
                budgetNameHolder.text = "Budget: ${obj.getString(1)}"
                var styleResId = R.style.budget_name_layout
                var styledTextView = ContextThemeWrapper(requireContext(), styleResId)
                budgetNameHolder.setTextAppearance(styledTextView, styleResId)
                budgetNameHolder.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val budgetHolder = TextView(activity)
                budgetHolder.text = "Final budget: $${obj.getString(3)}"
                styleResId = R.style.budget_item_layout
                styledTextView = ContextThemeWrapper(requireContext(), styleResId)
                budgetHolder.setTextAppearance(styledTextView, styleResId)
                budgetHolder.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val balanceHolder = TextView(activity)
                balanceHolder.text = "Final balance: $${obj.getString(4)}"
                styleResId = R.style.budget_item_layout
                styledTextView = ContextThemeWrapper(requireContext(), styleResId)
                balanceHolder.setTextAppearance(styledTextView, styleResId)
                balanceHolder.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                historyHolder.addView(budgetNameHolder)
                historyHolder.addView(budgetHolder)
                historyHolder.addView(balanceHolder)

                periodFlag = false
                categoryFlag = false
            }
        }
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        val percentBudget = ((totalBudget - totalBalance) / (totalBudget) * 100).toFloat()
        val percentBalance = ((totalBalance) / (totalBudget) * 100).toFloat()

        // Create data entries for the chart
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(percentBudget, "Percentage of budget"))
        entries.add(PieEntry(percentBalance, "Percentage of balance"))

        // Create a dataset with the entries
        val dataSet = PieDataSet(entries, "Finances overview all time")
        dataSet.colors = listOf(Color.GREEN, Color.RED)
        dataSet.valueTextSize = 15f
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val decimalFormat = DecimalFormat("#.##")
                val roundedNumber = decimalFormat.format(value)
                return "$roundedNumber%"
            }
        }
        dataSet.setValueTextColors(listOf(Color.BLACK, Color.WHITE))

        // Create a pie data object with the dataset
        val data = PieData(dataSet)

        // Customize chart properties
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.setDrawEntryLabels(false)
        pieChart.setCenterTextColor(Color.BLACK)

        pieChart.centerText = "Finances overview"

        // Set the data to the chart
        pieChart.data = data

        // Refresh the chart
        pieChart.invalidate()
        obj.close()
        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}