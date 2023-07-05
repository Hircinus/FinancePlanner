package com.example.finance

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.finance.db.DataManager
import com.example.finance.db.MonthManager
import com.example.finance.db.TransactionManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)
        val dm = DataManager(requireActivity())
        val mm = MonthManager(requireActivity())
        val tm = TransactionManager(requireActivity())

        // Get preferences
        // Get the SharedPreferences object
        val sharedPref = context?.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val username = sharedPref?.getBoolean("showCategoryInSelection", true)

        // Prepare spinner for all budgets
        var items = ArrayList<String>()
        var itemsId = ArrayList<Int>()
        var transactions: Cursor
        val budgets: Spinner = view.findViewById(R.id.budgetsHolder)
        val addTransactionBtn: Button = view.findViewById(R.id.addTransaction)
        val transactionAmount: EditText = view.findViewById(R.id.transactionAmountInput)
        val budgetId = budgets.selectedItemId.toInt()
        items = if (username == true) dm.getAllNamesWithCategory() else dm.getAllNames()
        itemsId = dm.getAllIds()
        var adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item, items
        )
        budgets.adapter = adapter
        budgets.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int, id: Long
            ) {
                Log.v("item", (parent.getItemAtPosition(position) as String))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
        if(!itemsId.isEmpty()) {
            transactions = tm.getAllByBudgetId(itemsId[budgetId])
        }
        addTransactionBtn.setOnClickListener {
            val amount = transactionAmount.text.toString().toDouble()
            tm.insert(itemsId[budgetId], amount)
        }
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
         * @return A new instance of fragment TransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}