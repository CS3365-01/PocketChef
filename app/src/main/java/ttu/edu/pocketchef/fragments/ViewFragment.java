package ttu.edu.pocketchef.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ttu.edu.pocketchef.R;
import ttu.edu.pocketchef.content.DB;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ViewFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private LayoutInflater inflater;

    public ViewFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_view, container, false);
        this.inflater = inflater;
        view = v;
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

    public void populateWithRecipe(long id) {
        Log.i("recipe", id + "");
        String selectQuery = "SELECT * FROM Recipe WHERE ID = " + id;
        Cursor c = DB.getDB().rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("Name"));
            String description = c.getString(c.getColumnIndex("Description"));
            String source = c.getString(c.getColumnIndex("Source"));
            String cookTime = c.getString(c.getColumnIndex("CookTime"));
            int rating = c.getInt(c.getColumnIndex("Rating"));

            TextView title = (TextView)view.findViewById(R.id.vw_title);
            TextView content = (TextView)view.findViewById(R.id.vw_content);
            TextView steps = (TextView)view.findViewById(R.id.vw_steps);
            TextView ing = (TextView)view.findViewById(R.id.vw_ing);

            title.setText(name);
            content.setText("Source: " + source + "\nCook Time: " + cookTime + "\nRating: " + rating + "\n\nDescription: " + description);

            Cursor ci = DB.getDB().rawQuery("SELECT * FROM IngredientRecipe WHERE RecipeID = " + id, null);
            StringBuilder sbi = new StringBuilder();
            sbi.append("Ingredients:\n\n");
            while (ci.moveToNext()) {
                String amount = ci.getString(ci.getColumnIndex("Amount"));
                long ingID = ci.getInt(ci.getColumnIndex("IngredientID"));

                String total = amount;

                Cursor cii = DB.getDB().rawQuery("SELECT * FROM Ingredient WHERE ID = " + ingID, null);
                while (cii.moveToNext()) {
                    total += " " + cii.getString(cii.getColumnIndex("Name"));
                }
                cii.close();

                sbi.append(total + "\n");
            }
            ci.close();

            Cursor cs = DB.getDB().rawQuery("SELECT * FROM Step WHERE RecipeID = " + id, null);
            StringBuilder sbs = new StringBuilder();
            sbs.append("Steps:\n\n");
            while (cs.moveToNext()) {
                String desc = cs.getString(cs.getColumnIndex("Description"));
                int order = cs.getInt(cs.getColumnIndex("StepOrder"));
                sbs.append(order + ") " + desc + "\n");
            }
            cs.close();

            steps.setText(sbs.toString());
            ing.setText(sbi.toString());
        }
        c.close();
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
        void onViewRecipeFragmentInteraction();
    }
}