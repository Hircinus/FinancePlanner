package com.example.finance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.settings -> {
                var frag = SettingsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder, frag)
                    .commit()
            }
            R.id.about -> {
                var frag = AboutFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder, frag)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
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