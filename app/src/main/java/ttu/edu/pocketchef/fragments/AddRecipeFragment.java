package ttu.edu.pocketchef.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

                if (name.getText().toString().equals("")) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("The recipe name can not be empty.")
                            .setPositiveButton("Ok", null)
                            .show();
                } else {
                    DB.getDB().execSQL("INSERT INTO Recipe (Name, Description) VALUES ('" + name.getText().toString() + "', '" + desc.getText().toString() + "')");

                    DB.dumpRecipes();

                    name.setText("");
                    desc.setText("");

                    if (mListener != null) {
                        mListener.onAddRecipeFragmentInteraction(true);
                    }
                }
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
