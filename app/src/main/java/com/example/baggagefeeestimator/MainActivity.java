package com.example.baggagefeeestimator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final double ECONOMY_LIMIT = 20;
    private static final double BUSINESS_LIMIT = 40;
    private static final double SERVICE_TAX = 0.08;

    private EditText etWeight, etRate;
    private RadioButton rbEconomy, rbBusiness;
    private TextView tvExcessWeight, tvExcessCharge, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Link views
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        rbEconomy = findViewById(R.id.rbEconomy);
        rbBusiness = findViewById(R.id.rbBusiness);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTotal = findViewById(R.id.tvTotal);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        Button btnReset = findViewById(R.id.btnReset);

        btnCalculate.setOnClickListener(v -> calculate());
        btnReset.setOnClickListener(v -> reset());
    }

    private void calculate() {
        String weightText = etWeight.getText().toString().trim();
        String rateText = etRate.getText().toString().trim();

        // Validation: empty fields
        if (weightText.isEmpty()) {
            etWeight.setError("Please enter the baggage weight");
            etWeight.requestFocus();
            return;
        }
        if (rateText.isEmpty()) {
            etRate.setError("Please enter the rate per kg");
            etRate.requestFocus();
            return;
        }

        // Validation: invalid numbers (e.g. just ".")
        double weight, rate;
        try {
            weight = Double.parseDouble(weightText);
            rate = Double.parseDouble(rateText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (weight <= 0) {
            etWeight.setError("Weight must be more than 0");
            etWeight.requestFocus();
            return;
        }

        // Calculation
        double limit = rbBusiness.isChecked() ? BUSINESS_LIMIT : ECONOMY_LIMIT;
        double excessWeight = Math.max(0, weight - limit);
        double excessCharge = excessWeight * rate;
        double total = excessCharge + (SERVICE_TAX * excessCharge);

        // Formatted output
        tvExcessWeight.setText(String.format("Excess weight: %.1f kg", excessWeight));
        tvExcessCharge.setText(String.format("Excess charge: RM %.2f", excessCharge));
        tvTotal.setText(String.format("Total payable: RM %.2f", total));

        // Helpful notice
        if (excessWeight == 0) {
            Toast.makeText(this, "Your baggage is within the " + (int) limit + " kg allowance. No charge.", Toast.LENGTH_LONG).show();
        }
    }

    private void reset() {
        etWeight.setText("");
        etRate.setText("");
        etWeight.setError(null);
        etRate.setError(null);
        rbEconomy.setChecked(true);
        tvExcessWeight.setText("Excess weight: 0.0 kg");
        tvExcessCharge.setText("Excess charge: RM 0.00");
        tvTotal.setText("Total payable: RM 0.00");
        etWeight.requestFocus();
    }
}