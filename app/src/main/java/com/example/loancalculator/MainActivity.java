package com.example.loancalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etBirthYear, etLoanAmount, etInterestRate, etNumRepayments;
    private Spinner spLoanType;
    private Button btnCalculate;
    private TextView tvMonthlyInstallment, tvTotalRepayment, tvLastPaymentDate;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBirthYear = findViewById(R.id.etBirthYear);
        etLoanAmount = findViewById(R.id.etLoanAmount);
        etInterestRate = findViewById(R.id.etInterestRate);
        etNumRepayments = findViewById(R.id.etNumRepayments);
        spLoanType = findViewById(R.id.spLoanType);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvMonthlyInstallment = findViewById(R.id.tvMonthlyInstallment);
        tvTotalRepayment = findViewById(R.id.tvTotalRepayment);
        tvLastPaymentDate = findViewById(R.id.tvLastPaymentDate);

        sharedPreferences = getSharedPreferences("LoanCalculatorPrefs", MODE_PRIVATE);

        String savedBirthYear = sharedPreferences.getString("birthYear", "");
        if (!savedBirthYear.isEmpty()) {
            etBirthYear.setText(savedBirthYear);
        }

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLoan();
            }
        });
    }

    private void calculateLoan() {
        int birthYear = Integer.parseInt(etBirthYear.getText().toString());
        double loanAmount = Double.parseDouble(etLoanAmount.getText().toString());
        double interestRate = Double.parseDouble(etInterestRate.getText().toString()) / 100;
        int numRepayments = Integer.parseInt(etNumRepayments.getText().toString());

        sharedPreferences.edit().putString("birthYear", etBirthYear.getText().toString()).apply();

        String loanType = spLoanType.getSelectedItem().toString();
        double monthlyInstallment = 0;
        int maxLoanAge = 0;

        if (loanType.equals("Personal Loan")) {
            maxLoanAge = 60;
            monthlyInstallment = calculatePersonalLoan(loanAmount, interestRate, numRepayments);
        } else if (loanType.equals("Housing Loan")) {
            maxLoanAge = 70;
            monthlyInstallment = calculateHousingLoan(loanAmount, interestRate, numRepayments);
        }

        int currentYear = 2024; // You can dynamically get the current year
        int age = currentYear - birthYear;
        int maxLoanTerm = (maxLoanAge - age) * 12;

        if (numRepayments > maxLoanTerm) {
            numRepayments = maxLoanTerm;
            Toast.makeText(this, "Adjusted repayment period due to age limit.", Toast.LENGTH_SHORT).show();
        }

        double totalRepayment = monthlyInstallment * numRepayments;

        tvMonthlyInstallment.setText(String.format("RM %.2f", monthlyInstallment));
        tvTotalRepayment.setText(String.format("RM %.2f", totalRepayment));
        tvLastPaymentDate.setText(String.format("%d months later", numRepayments));
    }

    private double calculatePersonalLoan(double P, double R, int N) {
        return P * Math.pow(1 + R / 12, N);
    }

    private double calculateHousingLoan(double P, double R, int N) {
        return (P * R / 12 * Math.pow(1 + R / 12, N)) / (Math.pow(1 + R / 12, N) - 1);
    }
}
