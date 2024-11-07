package com.mab.buwisbuddyph

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.mab.buwisbuddyph.home.HomeActivity

class TaxCalculatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tax_calculator)

        val grossIncomeInput: EditText = findViewById(R.id.monthlyIncomeET)
        val calculateButton: Button = findViewById(R.id.calculateButton)
        val sssContributionTextView: TextView = findViewById(R.id.sssContributionTextView)
        val philhealthContributionTextView: TextView = findViewById(R.id.philhealthContributionTextView)
        val pagIbigContributionTextView: TextView = findViewById(R.id.pagIbigContributionTextView)
        val monthlyIncomeTaxTextView: TextView = findViewById(R.id.monthlyIncomeTaxTextView)
        val quarterlyIncomeTaxTextView: TextView = findViewById(R.id.quarterlyIncomeTaxTextView)
        val annualIncomeTaxTextView: TextView = findViewById(R.id.annualIncomeTaxTextView)
        val resultTextView: TextView = findViewById(R.id.resultTextView)

        val returnIcon = findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@TaxCalculatorActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        calculateButton.setOnClickListener {
            val grossIncome = grossIncomeInput.text.toString().toDoubleOrNull() ?: 0.0

            val sssContribution = calculateSSSContribution(grossIncome)
            val philhealthContribution = calculatePhilHealthContribution(grossIncome)
            val pagIbigContribution = calculatePagIbigContribution(grossIncome)

            val totalDeductions = sssContribution + philhealthContribution + pagIbigContribution
            val netIncome = grossIncome - totalDeductions

            val graduatedIncomeTax = calculateGraduatedIncomeTax(netIncome)
            val flatIncomeTax = (netIncome - 250000) * 0.08 // Flat rate tax

            // Determine which tax method yields the lower tax amount
            val bestMethod = if (graduatedIncomeTax < flatIncomeTax) getString(R.string.graduated_income_tax) else getString(R.string.flat_income_tax)
            val bestTax = minOf(graduatedIncomeTax, flatIncomeTax)

            val monthlyIncomeTax = bestTax / 12
            val quarterlyIncomeTax = bestTax / 4

            sssContributionTextView.text = getString(R.string.contribution_format, sssContribution)
            philhealthContributionTextView.text = getString(R.string.contribution_format, philhealthContribution)
            pagIbigContributionTextView.text = getString(R.string.contribution_format, pagIbigContribution)
            monthlyIncomeTaxTextView.text = getString(R.string.tax_format, monthlyIncomeTax)
            quarterlyIncomeTaxTextView.text = getString(R.string.tax_format, quarterlyIncomeTax)
            annualIncomeTaxTextView.text = getString(R.string.tax_format, bestTax)

            resultTextView.text = getString(R.string.best_tax_method, bestMethod)
        }
    }

    private fun calculateSSSContribution(income: Double): Double {
        val sssContributionRate = 0.14 // 14%
        val maxMSC = 20000.0 // maximum monthly salary credit
        val msc = if (income > maxMSC) maxMSC else income
        return msc * sssContributionRate
    }

    private fun calculatePhilHealthContribution(income: Double): Double {
        val philhealthRate = 0.04 // 4%
        val minPremium = 400.0
        val maxPremium = 3200.0
        val contribution = income * philhealthRate
        return when {
            contribution < minPremium -> minPremium
            contribution > maxPremium -> maxPremium
            else -> contribution
        }
    }

    private fun calculatePagIbigContribution(income: Double): Double {
        val pagIbigRate = 0.02 // 2%
        val maxContribution = 100.0
        val contribution = income * pagIbigRate
        return if (contribution > maxContribution) maxContribution else contribution
    }

    private fun calculateGraduatedIncomeTax(netIncome: Double): Double {
        return when {
            netIncome <= 250000 -> 0.0
            netIncome <= 400000 -> (netIncome - 250000) * 0.20
            netIncome <= 800000 -> 30000 + (netIncome - 400000) * 0.25
            netIncome <= 2000000 -> 130000 + (netIncome - 800000) * 0.30
            netIncome <= 8000000 -> 490000 + (netIncome - 2000000) * 0.32
            else -> 2410000 + (netIncome - 8000000) * 0.35
        }
    }
}
