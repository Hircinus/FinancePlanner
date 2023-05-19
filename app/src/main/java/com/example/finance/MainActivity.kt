package com.example.finance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var frag = supportFragmentManager
            .findFragmentById(R.id.fragmentHolder)
        if(frag == null) {
            frag = HomeFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentHolder, frag)
                .commit()
        }
    }
    fun loadDashboard(v: View) {
        var frag = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
    fun loadManageBudgets(v: View) {
        var frag = ManageFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
    fun loadHistory(v: View) {
        var frag = HistoryFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
}