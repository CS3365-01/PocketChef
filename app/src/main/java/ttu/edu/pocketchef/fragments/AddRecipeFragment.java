package ttu.edu.pocketchef.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import ttu.edu.pocketchef.R;
import ttu.edu.pocketchef.content.DB;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddRecipeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddRecipeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private int orderInc = 1;
    public static final String TIMERANGEPICKER_TAG = "timerangepicker";

    public AddRecipeFragment() {
        // Required empty public constrctor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        final View d = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        Button b = (Button)v.findViewById(R.id.add_recipe_save);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = (EditText)v.findViewById(R.id.add_recipe_name);
                EditText desc = (EditText)v.findViewById(R.id.add_recipe_description);
                EditText cookTime = (EditText)v.findViewById(R.id.add_recipe_cooktime);
                EditText source = (EditText)v.findViewById(R.id.add_recipe_source);
                RatingBar rating = (RatingBar)v.findViewById(R.id.add_recipe_rating);
                LinearLayout ingredients = (LinearLayout)v.findViewById(R.id.add_recipe_ingredients_content);
                LinearLayout steps = (LinearLayout)v.findViewById(R.id.add_recipe_steps_content);

                if (name.getText().toString().equals("")) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("The recipe name can not be empty.")
                            .setPositiveButton("Ok", null)
                            .show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put("Name", name.getText().toString());
                    values.put("Description", desc.getText().toString());
                    values.put("CookTime", cookTime.getText().toString());
                    values.put("Source", source.getText().toString());
                    values.put("Rating", rating.getNumStars());
                    long id = DB.getDB().insert("Recipe", null, values);
                    Log.i("db", id + " id ");

                    for (int i = 0; i < ingredients.getChildCount(); i++) {
                        ViewGroup child = (ViewGroup)ingredients.getChildAt(i);
                        EditText quantity = (EditText)child.findViewById(R.id.add_recipe_ing_am);
                        AutoCompleteTextView ingName = (AutoCompleteTextView)child.findViewById(R.id.add_recipe_ing_name);

                        long ingId = -1;
                        String selectQuery = "SELECT * FROM Ingredient WHERE Name LIKE '" + ingName.getText().toString().toLowerCase() + "'";
                        Cursor c = DB.getDB().rawQuery(selectQuery, null);
                        while (c.moveToNext()) {
                            ingId = c.getLong(c.getColumnIndex("ID"));
                        }
                        c.close();

                        if (ingId == -1) {
                            ContentValues cv = new ContentValues();
                            cv.put("Name", ingName.getText().toString());
                            ingId = DB.getDB().insert("Ingredient", null, cv);
                        }

                        values = new ContentValues();
                        values.put("IngredientID", ingId);
                        values.put("RecipeID", id);
                        values.put("Amount", quantity.getText().toString());

                        DB.getDB().insert("IngredientRecipe", null, values);
                    }

                    for (int i = 0; i < steps.getChildCount(); i++) {
                        ViewGroup child = (ViewGroup)steps.getChildAt(i);
                        EditText stepDesc = (EditText)child.findViewById(R.id.add_step_ing_name);
                        TextView stepCount = (TextView)child.findViewById(R.id.input_layout_step_count);
                        values = new ContentValues();
                        values.put("Description", stepDesc.getText().toString());
                        values.put("RecipeID", id);
                        // parse incase it decided to get out of order. Could have used "i + 1" instead.
                        values.put("StepOrder", Integer.parseInt(stepCount.getText().toString()));

                        DB.getDB().insert("Step", null, values);
                    }

                    DB.dumpRecipes();

                    name.setText("");
                    desc.setText("");
                    steps.removeAllViewsInLayout();
                    source.setText("");
                    source.setText("");
                    cookTime.setText("");
                    rating.setNumStars(0);
                    ingredients.removeAllViewsInLayout();
                    orderInc = 1;

                    if (mListener != null) {
                        mListener.onAddRecipeFragmentInteraction(true);
                    }
                }
            }
        });

        Button addIng = (Button)v.findViewById(R.id.add_recipe_add_ing);
        addIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup ingredientArea = (ViewGroup)v.findViewById(R.id.add_recipe_ingredients_content);

                ViewGroup row = (ViewGroup) inflater.inflate(R.layout.content_ingredient_area, null);

                ArrayList<String> ings = new ArrayList<String>();
                String selectQuery = "SELECT * FROM Ingredient";
                Cursor c = DB.getDB().rawQuery(selectQuery, null);
                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex("Name"));
                    ings.add(name);
                }
                c.close();

                AutoCompleteTextView ing = (AutoCompleteTextView) row.findViewById(R.id.add_recipe_ing_name);
                String[] ingsa = ings.toArray(new String[ings.size()]);
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ingsa);
                ing.setAdapter(adapter);

                ingredientArea.addView(row);
            }
        });

        Button addStep = (Button)v.findViewById(R.id.add_recipe_add_step);
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup stepArea = (ViewGroup)v.findViewById(R.id.add_recipe_steps_content);

                ViewGroup row = (ViewGroup) inflater.inflate(R.layout.content_step_area, null);

                TextView order = (TextView)row.findViewById(R.id.input_layout_step_count);
                order.setText("" + orderInc++);

                stepArea.addView(row);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAddRecipeFragmentInteraction(boolean saved);
    }
}
