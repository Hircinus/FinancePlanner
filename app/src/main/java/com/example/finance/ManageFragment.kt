package com.example.finance

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_manage, container, false)
        val dm = DataManager(requireActivity())
        val mm = MonthManager(requireActivity())
        val addBalance: Button = view.findViewById(R.id.addBalance)
        val removeBalance: Button = view.findViewById(R.id.removeBalance)
        val addBudget: Button = view.findViewById(R.id.addBudget)
        val removeBudget: Button = view.findViewById(R.id.removeBudget)
        val deleteBudget: Button = view.findViewById(R.id.deleteBudget)
        val createBudget: Button = view.findViewById(R.id.createBudget)
        val updateStuff: LinearLayout = view.findViewById(R.id.updateBudgetMenu)
        val createStuff: LinearLayout = view.findViewById(R.id.createBudgetMenu)
        updateStuff.visibility = View.VISIBLE
        createStuff.visibility = View.GONE
        var items = ArrayList<Int>()
        val budgets: RadioGroup = view.findViewById(R.id.budgetsHolder)
        items = checkBudgetOptions(dm, mm, budgets, items)
        createBudget.setOnClickListener {
            updateStuff.visibility = View.GONE
            createStuff.visibility = View.VISIBLE
            val budgetName: EditText = view.findViewById(R.id.budgetName)
            val budgetCategory: EditText = view.findViewById(R.id.budgetCategory)
            val budgetAmount: EditText = view.findViewById(R.id.budgetAmount)
            val createBtn: Button = view.findViewById(R.id.createBudgetBtn)
            val goBack: Button = view.findViewById(R.id.goBackBtn)
            goBack.setOnClickListener {
                updateStuff.visibility = View.VISIBLE
                createStuff.visibility = View.GONE
            }
            createBtn.setOnClickListener {
                var obj = mm.getLast()
                if(budgetName.text.toString() == "" || budgetAmount.text.toString() == "" || budgetCategory.text.toString() == "") {
                    throwErrorDialog("Cannot create new budget as some fields are empty or invalid.", "Invalid budget properties") {}
                } else {
                    if(obj.moveToLast() && obj.getDouble(2) > System.currentTimeMillis()) {
                        dm.insert(budgetName.text.toString(), budgetAmount.text.toString().toDouble(), budgetCategory.text.toString(), obj.getInt(0))
                        Toast.makeText(activity, "Budget successfully created", Toast.LENGTH_LONG).show();
                    } else {
                        val currentTime = System.currentTimeMillis()
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.timeInMillis = currentTime
                        calendar.add(Calendar.MONTH, 1)
                        val nextTime: Long = calendar.getTimeInMillis()
                        if(!obj.moveToLast()) {
                            mm.insert(currentTime, nextTime)
                        }
                        obj.close()
                        obj = mm.getLast()
                        dm.insert(budgetName.text.toString(), budgetAmount.text.toString().toDouble(), budgetCategory.text.toString(), obj.getInt(0))
                        Toast.makeText(activity, "Budget successfully created", Toast.LENGTH_LONG).show();
                    }
                }
                updateStuff.visibility = View.VISIBLE
                createStuff.visibility = View.GONE
                budgets.clearCheck()
                budgets.removeAllViews()
                items = checkBudgetOptions(dm, mm, budgets, items)
                obj.close()
            }
        }
        deleteBudget.setOnClickListener {
            if(budgets.checkedRadioButtonId > 0) {
                dm.delete(items[budgets.checkedRadioButtonId - 1])
                Toast.makeText(activity, "Budget successfully deleted", Toast.LENGTH_LONG).show();
            } else {
                invalidBudgetSelected()
            }
            budgets.clearCheck()
            budgets.removeAllViewsInLayout()
            items = checkBudgetOptions(dm, mm, budgets, items)
        }
        removeBudget.setOnClickListener {
            Log.i("currentID", "${budgets.checkedRadioButtonId}")
            val amountEdit = view.findViewById<EditText>(R.id.amountBudget)
            if(budgets.checkedRadioButtonId > 0) {
                var amount: Double = 0.0
                if(amountEdit.text.toString() == "") {
                    throwErrorDialog("Please enter a number to remove from your budget.",
                        "No number entered"
                    ) { amountEdit.setText("", TextView.BufferType.EDITABLE) }
                }
                else {
                    amount = amountEdit.text.toString().toDouble()
                    var obj = dm.searchID(items[budgets.checkedRadioButtonId - 1])
                    if(obj.moveToLast()) {
                        if(amount > obj.getDouble(3)) {
                            throwErrorDialog("You can't remove that from your budget since your budget would become negative.",
                                "Budget adjustment invalid"
                            ) { amountEdit.setText("", TextView.BufferType.EDITABLE) }
                        } else {
                            dm.updateBudget(obj.getInt(0), (obj.getDouble(3) - amount))
                            Toast.makeText(activity, "$amount removed from the balance of ${obj.getString(1)}", Toast.LENGTH_LONG).show();
                        }
                    }
                    obj.close()
                }
            }
            else {
                invalidBudgetSelected()
            }
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        addBudget.setOnClickListener {
            Log.i("currentID", "${budgets.checkedRadioButtonId}")
            val amountEdit = view.findViewById<EditText>(R.id.amountBudget)
            if(budgets.checkedRadioButtonId > 0) {

                var amount: Double = 0.0
                if(amountEdit.text.toString() == "") {
                    throwErrorDialog("Please enter a number to add to your budget.",
                        "No number entered") {amountEdit.setText("", TextView.BufferType.EDITABLE)}
                }
                else {
                    amount = amountEdit.text.toString().toDouble()
                    var obj = dm.searchID(items[budgets.checkedRadioButtonId - 1])
                    if(obj.moveToLast()) {
                        dm.updateBudget(obj.getInt(0), (obj.getDouble(3) + amount))
                        Toast.makeText(activity, "$amount added to the budget of ${obj.getString(1)}", Toast.LENGTH_LONG).show();
                    }
                    obj.close()
                }
            } else {
                invalidBudgetSelected()
            }
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        removeBalance.setOnClickListener {
            Log.i("currentID", "${budgets.checkedRadioButtonId}")
            val amountEdit = view.findViewById<EditText>(R.id.amountToAdd)
            if(budgets.checkedRadioButtonId > 0) {

                var amount: Double = 0.0
                if(amountEdit.text.toString() == "") {
                    throwErrorDialog("Please enter a number to remove from your balance.",
                        "No number entered"
                    ) { amountEdit.setText("", TextView.BufferType.EDITABLE) }
                }
                else {
                    amount = amountEdit.text.toString().toDouble()
                    var obj = dm.searchID(items[budgets.checkedRadioButtonId - 1])
                    if(obj.moveToLast()) {
                        if(amount > obj.getDouble(4)) {
                            throwErrorDialog("You can't remove that from your balance since your balance would become negative.",
                                "Balance adjustment invalid"
                            ) { amountEdit.setText("", TextView.BufferType.EDITABLE) }
                        } else {
                            dm.updateBalance(obj.getInt(0), (obj.getDouble(4) - amount))
                            Toast.makeText(activity, "$amount removed from the balance of ${obj.getString(1)}", Toast.LENGTH_LONG).show();
                        }
                    }
                    obj.close()
                }
            }
            else {
                invalidBudgetSelected()
            }
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        addBalance.setOnClickListener {
            Log.i("currentID", "${budgets.checkedRadioButtonId}")
            val amountEdit = view.findViewById<EditText>(R.id.amountToAdd)
            if(budgets.checkedRadioButtonId > 0) {

                var amount: Double = 0.0
                if(amountEdit.text.toString() == "") {
                    throwErrorDialog("Please enter a number to add to your balance.",
                        "No number entered") {amountEdit.setText("", TextView.BufferType.EDITABLE)}
                }
                else {
                    amount = amountEdit.text.toString().toDouble()
                    var obj = dm.searchID(items[budgets.checkedRadioButtonId - 1])
                    if(obj.moveToLast()) {
                        if(amount > (obj.getDouble(3) - obj.getDouble(4))) {
                            throwErrorDialog("You can't add that to your balance since your budget would become negative.",
                                "Balance adjustment invalid") {amountEdit.setText("", TextView.BufferType.EDITABLE)}
                        } else {
                            dm.updateBalance(obj.getInt(0), (obj.getDouble(4) + amount))
                            Toast.makeText(activity, "$$amount added to the balance of ${obj.getString(1)}", Toast.LENGTH_LONG).show();
                        }
                    }
                    obj.close()
                }
            } else {
                invalidBudgetSelected()
            }
            budgets.clearCheck()
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun checkBudgetOptions(dm: DataManager, mm: MonthManager, budgets: RadioGroup, items: ArrayList<Int>): ArrayList<Int> {
        val budgetOptions = dm.selectAll()
        val currentMonth = mm.getLast()
        budgets.clearCheck()
        budgets.removeAllViews()
        var count = 1
        items.clear()
        while(budgetOptions.moveToNext()) {
            if(currentMonth.moveToLast()) {
                if(currentMonth.getInt(0) == budgetOptions.getInt(5)) {
                    items.add(budgetOptions.getInt(0))
                    val budget = RadioButton(activity)
                    budget.id = count
                    budget.text = "${budgetOptions.getString(1)} (${budgetOptions.getString(2)})"
                    budget.layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                    count++
                    budgets.addView(budget)
                }
            }
        }
        budgetOptions.close()
        currentMonth.close()
        return items
    }

    fun throwErrorDialog(message: String, title: String, run: () -> Unit) {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            .setPositiveButton("Try again", DialogInterface.OnClickListener {
                    dialog, id ->
                run {
                    run()
                    dialog.dismiss()
                }

            })

        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }
    fun invalidBudgetSelected() {
        throwErrorDialog("Please select a budget to edit.", "Budget not selected") {}
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ManageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}