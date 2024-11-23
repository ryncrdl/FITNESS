package com.easyfitness;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.easyfitness.DAO.DatabaseHelper;
import com.easyfitness.licenses.CustomLicense;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.BSD3ClauseLicense;
import de.psdev.licensesdialog.licenses.GnuLesserGeneralPublicLicense21;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;


public class AboutFragment extends Fragment {
    private String name;
    private int id;
    private MainActivity mActivity = null;

    private final View.OnClickListener clickLicense = v -> {

        String name = null;
        String url = null;
        String copyright = null;
        License license = null;




    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static AboutFragment newInstance(String name, int id) {
        AboutFragment f = new AboutFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_about, container, false);

        Spinner spinnerGender = view.findViewById(R.id.spinner_gender);
        EditText etAge = view.findViewById(R.id.edit_age);
        EditText etWeight = view.findViewById(R.id.edit_weight);
        EditText etHeight = view.findViewById(R.id.edit_height);
        Spinner spinnerActivityLevel = view.findViewById(R.id.spinner_activity_level);
        EditText etWeightToLose = view.findViewById(R.id.edit_weight_to_lose);
        EditText etDays = view.findViewById(R.id.edit_days);
        EditText etProteinIntake = view.findViewById(R.id.edit_protein_intake);
        Button btnCalculate = view.findViewById(R.id.btn_generate_forecast);
        TextView tvResults = view.findViewById(R.id.tv_results);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get input values
                    String gender = spinnerGender.getSelectedItem().toString();
                    int age = Integer.parseInt(etAge.getText().toString());
                    double weight = Double.parseDouble(etWeight.getText().toString());
                    int height = Integer.parseInt(etHeight.getText().toString());
                    String activityLevel = spinnerActivityLevel.getSelectedItem().toString();
                    double weightToLose = Double.parseDouble(etWeightToLose.getText().toString());
                    int days = Integer.parseInt(etDays.getText().toString());
                    int proteinIntake = Integer.parseInt(etProteinIntake.getText().toString());

                    // Calculate BMR
                    double bmr = gender.equalsIgnoreCase("Male")
                            ? (10 * weight) + (6.25 * height) - (5 * age) + 5
                            : (10 * weight) + (6.25 * height) - (5 * age) - 161;

                    // Activity level multiplier
                    double pal;
                    switch (activityLevel) {
                        case "Lightly Active":
                            pal = 1.375;
                            break;
                        case "Moderately Active":
                            pal = 1.55;
                            break;
                        case "Very Active":
                            pal = 1.725;
                            break;
                        case "Extremely Active":
                            pal = 1.9;
                            break;
                        default:
                            pal = 1.2; // Sedentary
                            break;
                    }

                    double dailyCalorieNeeds = bmr * pal;
                    double weeklyCalorieNeeds = dailyCalorieNeeds * 7;
                    double weightLossRate = weightToLose / days;

                    // Generate health tips
                    String healthTip;
                    if (weightLossRate > 0.5) {
                        healthTip = "Rapid weight loss can be harmful to your health.";
                    } else if (weightLossRate < 0.1) {
                        healthTip = "Your weight loss rate is slow but steady.";
                    } else {
                        healthTip = "Your weight loss rate seems reasonable.";
                    }

                    // Display results
                    String results = String.format(
                            "Daily Calorie Needs: %.2f\nWeekly Calorie Needs: %.2f\nWeight Loss Rate: %.2f kg/day\nHealth Tip:\n%s",
                            dailyCalorieNeeds, weeklyCalorieNeeds, weightLossRate, healthTip);
                    tvResults.setText(results);

                } catch (Exception e) {
                    tvResults.setText("Please fill out all fields correctly.");
                }
            }
        });

        return view;
    }

    public MainActivity getMainActivity() {
        return this.mActivity;
    }

}

