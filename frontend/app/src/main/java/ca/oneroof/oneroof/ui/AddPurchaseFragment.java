package ca.oneroof.oneroof.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

import ca.oneroof.oneroof.R;
import ca.oneroof.oneroof.api.Division;
import ca.oneroof.oneroof.api.House;
import ca.oneroof.oneroof.api.Purchase;
import ca.oneroof.oneroof.databinding.FragmentAddPurchaseBinding;
import ca.oneroof.oneroof.viewmodel.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPurchaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPurchaseFragment extends Fragment {
    private HouseViewModel viewmodel;

    private EditText memoText;

    public MutableLiveData<Integer> totalAmount = new MutableLiveData<>(0);
    private ArrayList<DivisionEdit> divisions = new ArrayList<>();
    private ListView divisionList;
    private DivisionEditAdapter divisionEditAdapter;

    public static String formatDollars(int cents) {
        return String.format("%d.%02d", cents / 100, cents % 100);
    }

    public AddPurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewmodel = new ViewModelProvider(getActivity()).get(HouseViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentAddPurchaseBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_add_purchase, container, false);
        binding.setViewmodel(viewmodel);
        binding.setFragment(this);
        binding.setLifecycleOwner(this);

        View view = binding.getRoot();

        memoText = view.findViewById(R.id.memo_text);

        divisionList = view.findViewById(R.id.division_list);
        divisionEditAdapter = new DivisionEditAdapter(getContext(), R.layout.item_division_edit, divisions, totalAmount);
        divisionList.setAdapter(divisionEditAdapter);

        return view;
    }

    public void clickAddDivision(View v) {
        divisionEditAdapter.add(new DivisionEdit(
                viewmodel.house.data.getValue().data.roommate_names,
                viewmodel.house.data.getValue().data.roommates));
    }

    public void clickSavePurchase(View v) {
        Purchase purchase = new Purchase();
        purchase.memo = memoText.getText().toString();
        purchase.purchaser = viewmodel.roommateId.getValue();
        purchase.divisions = new ArrayList<>();
        purchase.amount = totalAmount.getValue();

        for (DivisionEdit edit : divisions) {
            Division division = new Division();
            division.amount = edit.amount;
            division.roommates = new ArrayList<>();
            for (int i = 0; i < edit.roommateEnables.size(); i++) {
                if (edit.roommateEnables.get(i)) {
                    division.roommates.add(edit.roommates.get(i));
                }
            }
            purchase.divisions.add(division);
        }

        viewmodel.postPurchase(purchase);

        Navigation.findNavController(getView())
                .popBackStack();
    }
}