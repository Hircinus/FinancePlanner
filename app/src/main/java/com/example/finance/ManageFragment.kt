package com.example.finance

import android.content.Context
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
        // Initialize view variables
        val view = inflater.inflate(R.layout.fragment_manage, container, false)
        val dm = DataManager(requireActivity())
        val mm = MonthManager(requireActivity())
        val toggleBalance: Button = view.findViewById(R.id.toggleBalance)
        val addBalance: Button = view.findViewById(R.id.addBalance)
        val removeBalance: Button = view.findViewById(R.id.removeBalance)
        val toggleBudget: Button = view.findViewById(R.id.toggleBudget)
        val addBudget: Button = view.findViewById(R.id.addBudget)
        val removeBudget: Button = view.findViewById(R.id.removeBudget)
        val deleteBudget: Button = view.findViewById(R.id.deleteBudget)
        val createBudget: Button = view.findViewById(R.id.createBudget)
        // Variables for multiple appearing/disappearing containers
        val updateStuff: LinearLayout = view.findViewById(R.id.updateBudgetMenu)
        val createStuff: LinearLayout = view.findViewById(R.id.createBudgetMenu)
        val balanceContainer: LinearLayout = view.findViewById(R.id.balanceContainer)
        val budgetContainer: LinearLayout = view.findViewById(R.id.budgetContainer)
        // Set default visibility
        updateStuff.visibility = View.VISIBLE
        createStuff.visibility = View.GONE
        balanceContainer.visibility = View.GONE
        budgetContainer.visibility = View.GONE

        // Toggle each container with their respective buttons
        toggleBalance.setOnClickListener {
            if(balanceContainer.visibility == View.GONE) {
                balanceContainer.visibility = View.VISIBLE
                budgetContainer.visibility = View.GONE
            }
            else
                balanceContainer.visibility = View.GONE
        }
        toggleBudget.setOnClickListener {
            if (budgetContainer.visibility == View.GONE) {
                budgetContainer.visibility = View.VISIBLE
                balanceContainer.visibility = View.GONE
            }
            else
                budgetContainer.visibility = View.GONE
        }

        // Get preferences
        // Get the SharedPreferences object
        val sharedPref = context?.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val username = sharedPref?.getBoolean("showCategoryInSelection", true)

        // Prepare spinner for all budgets
        var items = ArrayList<String>()
        var itemsId = ArrayList<Int>()
        val budgets: Spinner = view.findViewById(R.id.budgetsHolder)
        items = if (username == true) checkBudgetOptionsByNameWithCategory(dm, mm) else checkBudgetOptionsByName(dm, mm)
        itemsId = checkBudgetOptionsById(dm, mm)
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

        createBudget.setOnClickListener {
            updateStuff.visibility = View.GONE
            createStuff.visibility = View.VISIBLE
            val budgetName: EditText = view.findViewById(R.id.budgetName)
            val budgetCategory = view.findViewById(R.id.budget_category_picker) as Spinner
            val newBudgetCategory = view.findViewById<EditText>(R.id.categoryName)

            // prepare category options spinner
            var options = checkCategoryOptions(dm, mm)
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_item, options
            )

            budgetCategory.adapter = adapter

            budgetCategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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
            val budgetAmount: EditText = view.findViewById(R.id.budgetAmount)
            val createBtn: Button = view.findViewById(R.id.createBudgetBtn)
            val goBack: Button = view.findViewById(R.id.goBackBtn)
            goBack.setOnClickListener {
                updateStuff.visibility = View.VISIBLE
                createStuff.visibility = View.GONE
            }
            createBtn.setOnClickListener {
                var obj = mm.getLast()
                if(budgetName.text.toString() == "" || budgetAmount.text.toString() == "") {
                    throwErrorDialog("Cannot create new budget as some fields are empty or invalid.", "Invalid budget properties") {}
                } else {
                    if(obj.moveToLast() && obj.getDouble(2) > System.currentTimeMillis()) {
                        val monthId = obj.getInt(0)
                        if(newBudgetCategory.text.toString() != "") {
                            if(!categoryExists(dm, newBudgetCategory.text.toString())) {
                                val dialogBuilder = AlertDialog.Builder(requireActivity())
                                dialogBuilder.setMessage("Are you sure you want to create a new category called ${newBudgetCategory.text.toString()}?")
                                    // if the dialog is cancelable
                                    .setCancelable(false)
                                    .setPositiveButton("Yes, make it", DialogInterface.OnClickListener {
                                            dialog, id ->
                                        run {
                                            dm.insert(budgetName.text.toString(), budgetAmount.text.toString().toDouble(), newBudgetCategory.text.toString(), monthId)
                                            items = if (username == true) checkBudgetOptionsByNameWithCategory(dm, mm) else checkBudgetOptionsByName(dm, mm)
                                            itemsId = checkBudgetOptionsById(dm, mm)
                                            budgets.adapter = ArrayAdapter<String>(
                                                requireContext(),
                                                R.layout.spinner_item, items
                                            )
                                            dialog.dismiss()
                                        }

                                    })
                                    .setNegativeButton("Nevermind", DialogInterface.OnClickListener() {
                                            dialog, id ->
                                        run {
                                            dialog.dismiss()
                                        }
                                    })
                                val alert = dialogBuilder.create()
                                alert.setTitle("New category inputted")
                                alert.show()

                            } else {
                                throwErrorDialog("Cannot create category ${newBudgetCategory.text.toString()} since it already exists.", "Category already exists") {}
                            }
                        } else {
                            dm.insert(budgetName.text.toString(), budgetAmount.text.toString().toDouble(), budgetCategory.selectedItem.toString(), obj.getInt(0))
                        }
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
                        dm.insert(budgetName.text.toString(), budgetAmount.text.toString().toDouble(), budgetCategory.selectedItem.toString(), obj.getInt(0))
                        Toast.makeText(activity, "Budget successfully created", Toast.LENGTH_LONG).show();
                    }
                }
                updateStuff.visibility = View.VISIBLE
                createStuff.visibility = View.GONE
                items = if (username == true) checkBudgetOptionsByNameWithCategory(dm, mm) else checkBudgetOptionsByName(dm, mm)
                itemsId = checkBudgetOptionsById(dm, mm)
                budgets.adapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.spinner_item, items
                )
                obj.close()
            }
        }
        deleteBudget.setOnClickListener {
            showConfirmationDialog("Are you sure you want to delete this budget?", "Confirm budget deletion", "Delete it", "Show it mercy", dm) {
                dm.delete(budgets.selectedItem.toString())
                Toast.makeText(activity, "Budget successfully deleted", Toast.LENGTH_LONG).show();
                items = if (username == true) checkBudgetOptionsByNameWithCategory(dm, mm) else checkBudgetOptionsByName(dm, mm)
                itemsId = checkBudgetOptionsById(dm, mm)
                budgets.adapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.spinner_item, items
                )
            }
        }
        removeBudget.setOnClickListener {
            val amountEdit = view.findViewById<EditText>(R.id.amountBudget)
            var amount: Double = 0.0
            if(amountEdit.text.toString() == "") {
                throwErrorDialog("Please enter a number to remove from your budget.",
                    "No number entered"
                ) { amountEdit.setText("", TextView.BufferType.EDITABLE) }
            }
            else {
                amount = amountEdit.text.toString().toDouble()
                val obj = dm.searchID(itemsId[budgets.selectedItemId.toInt()])
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
                items = if (username == true) checkBudgetOptionsByNameWithCategory(dm, mm) else checkBudgetOptionsByName(dm, mm)
                itemsId = checkBudgetOptionsById(dm, mm)
                budgets.adapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.spinner_item, items
                )
                obj.close()
            }
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        addBudget.setOnClickListener {
            val amountEdit = view.findViewById<EditText>(R.id.amountBudget)
            var amount: Double = 0.0
            if(amountEdit.text.toString() == "") {
                throwErrorDialog("Please enter a number to add to your budget.",
                    "No number entered") {amountEdit.setText("", TextView.BufferType.EDITABLE)}
            }
            else {
                amount = amountEdit.text.toString().toDouble()
                var obj = dm.searchID(itemsId[budgets.selectedItemId.toInt()])
                if(obj.moveToLast()) {
                    dm.updateBudget(obj.getInt(0), (obj.getDouble(3) + amount))
                    Toast.makeText(activity, "$amount added to the budget of ${obj.getString(1)}", Toast.LENGTH_LONG).show();
                }
                obj.close()
            }
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        removeBalance.setOnClickListener {
            val amountEdit = view.findViewById<EditText>(R.id.amountToAdd)
            var amount: Double = 0.0
            if(amountEdit.text.toString() == "") {
                throwErrorDialog("Please enter a number to remove from your balance.",
                    "No number entered"
                ) { amountEdit.setText("", TextView.BufferType.EDITABLE) }
            }
            else {
                amount = amountEdit.text.toString().toDouble()
                var obj = dm.searchID(itemsId[budgets.selectedItemId.toInt()])
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
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        addBalance.setOnClickListener {
            val amountEdit = view.findViewById<EditText>(R.id.amountToAdd)
            var amount: Double = 0.0
            if(amountEdit.text.toString() == "") {
                throwErrorDialog("Please enter a number to add to your balance.",
                    "No number entered") {amountEdit.setText("", TextView.BufferType.EDITABLE)}
            }
            else {
                amount = amountEdit.text.toString().toDouble()
                var obj = dm.searchID(itemsId[budgets.selectedItemId.toInt()])
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
            amountEdit.setText("", TextView.BufferType.EDITABLE)
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun categoryExists(dm: DataManager, text: String): Boolean {
        val budgetOptions = dm.selectAllByCategory()
        while(budgetOptions.moveToNext()) {
            if(budgetOptions.getString(2).toString().lowercase() == text.lowercase())
                return true
        }
        return false
    }

    private fun checkBudgetOptionsByNameWithCategory(dm: DataManager, mm: MonthManager): ArrayList<String> {
        val budgetOptions = dm.selectAll()
        val lastMonth = mm.getLast()
        val items: ArrayList<String> = ArrayList()
        while(budgetOptions.moveToNext()) {
            val budgetName = budgetOptions.getString(1)
            val category = budgetOptions.getString(2)
            if(lastMonth.moveToLast()) {
                if(budgetOptions.getInt(5) == lastMonth.getInt(0)) {
                    items.add("$budgetName ($category)")
                }
            }
        }
        lastMonth.close()
        budgetOptions.close()
        return items
    }
    private fun checkBudgetOptionsByName(dm: DataManager, mm: MonthManager): ArrayList<String> {
        val budgetOptions = dm.selectAll()
        val lastMonth = mm.getLast()
        val items: ArrayList<String> = ArrayList()
        while(budgetOptions.moveToNext()) {
            val budgetName = budgetOptions.getString(1)
            if(lastMonth.moveToLast()) {
                if(budgetOptions.getInt(5) == lastMonth.getInt(0)) {
                    items.add(budgetName)
                }
            }
        }
        lastMonth.close()
        budgetOptions.close()
        return items
    }
    private fun checkBudgetOptionsById(dm: DataManager, mm: MonthManager): ArrayList<Int> {
        val budgetOptions = dm.selectAll()
        val lastMonth = mm.getLast()
        val items: ArrayList<Int> = ArrayList()
        while(budgetOptions.moveToNext()) {
            val budgetId = budgetOptions.getInt(0)
            if(lastMonth.moveToLast()) {
                if(budgetOptions.getInt(5) == lastMonth.getInt(0)) {
                    items.add(budgetId)
                }
            }
        }
        budgetOptions.close()
        lastMonth.close()
        return items
    }
    private fun checkCategoryOptions(dm: DataManager, mm: MonthManager): ArrayList<String> {
        val budgetOptions = dm.selectAll()
        var mem = ""
        var items: ArrayList<String> = ArrayList()
        while(budgetOptions.moveToNext()) {
            var currentCategory = budgetOptions.getString(2)
            if(currentCategory != mem) {
                mem = currentCategory
                items.add(currentCategory)
            }
        }
        budgetOptions.close()
        return items
    }


    fun showConfirmationDialog(message: String, title: String, confirmation: String, dismissal: String, dm: DataManager, run: () -> Unit) {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            .setPositiveButton(confirmation, DialogInterface.OnClickListener {
                    dialog, id ->
                run {
                    run()
                    dialog.dismiss()
                }

            })
            .setNegativeButton(dismissal, DialogInterface.OnClickListener() {
                dialog, id ->
                run {
                    dialog.dismiss()
                }
            })
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
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