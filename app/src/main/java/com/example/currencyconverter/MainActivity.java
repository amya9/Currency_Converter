package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    MaterialSpinner firstCountry;
    MaterialSpinner secondCountry;
    EditText amountToConvert;
    Button convert;
    EditText convertedAmountET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstCountry = (MaterialSpinner) findViewById(R.id.spnFirstCountry);
        secondCountry = findViewById(R.id.spnSecondCountry);
        amountToConvert = findViewById(R.id.etFirstCurrency);
        convert = findViewById(R.id.btnConvert);
        convertedAmountET = findViewById(R.id.etSecondCurrency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstCountry.setAdapter(adapter);
        firstCountry.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        secondCountry.setAdapter(adapter);
        secondCountry.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view , "converted" , Snackbar.LENGTH_LONG).show();
//                convertedAmount.setText("29");
                if(amountToConvert.getText().toString().isEmpty() || amountToConvert.getText() == null){
                    amountToConvert.setError("Required");
                }else{
                    convert(view);
                }
            }
        });

    }

    public void convert(View view) {
        String fromCurrency = firstCountry.getText().toString();
        String toCurrency = secondCountry.getText().toString();
        String amount = amountToConvert.getText().toString();
        // Execute the AsyncTask to get the conversion rate in the background
        new ConversionTask().execute(fromCurrency, toCurrency, amount);
//        convertedAmount.setText((CharSequence) convertedAmounts);
    }

    private class ConversionTask extends AsyncTask<String, Void, Double> {
        @Override
        protected Double doInBackground(String... params) {
            String fromCurrency = params[0];
            String toCurrency = params[1];
            String amount = params[2];
            // Make a request to the API to get the conversion rate
            String urlString = "https://api.exchangerate-api.com/v4/latest/" + fromCurrency;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                String response = result.toString();
                Log.e("convertedAmount ", response);
                try {
                    // Parse the response as a JSON object
                    JSONObject responseJson = new JSONObject(response);
                    // Get the rates object from the response
                    JSONObject rates = responseJson.getJSONObject("rates");
                    // Get the conversion rate for the desired currency
                    double rate = rates.getDouble(toCurrency);
                    // Calculate the converted amount
                    double convertedAmount = Integer.valueOf(amount) * rate;
                    return convertedAmount;
                } catch (JSONException e) {
                    // Handle any JSON parsing exceptions
                }

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Double result) {
        // Use the result to update the UI or do something else
        convertedAmountET.setText((result).toString());
        }
    }
}