package com.example.inclassassignments.InClass03;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inclassassignments.InClass02.Profile;
import com.example.inclassassignments.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InClass03Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InClass03Main extends Fragment {

    private EditText editTextName;
    private EditText editTextEmail;
    private ImageView imageViewAvatar;
    private RadioGroup radioGroupOS;
    private TextView textViewMood;
    private SeekBar seekBarMood;
    private ImageView imageViewMood;
    private Button buttonSubmit;
    private int avatarNumber;
    public IFromMainToActivity sendData;

    public InClass03Main() {
        // Required empty public constructor
    }

    public static InClass03Main newInstance() {
        return new InClass03Main();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class03_main, container, false);
        editTextName = rootView.findViewById(R.id.editTextFragmentName);
        editTextEmail = rootView.findViewById(R.id.editTextFragmentEmail);
        imageViewAvatar = rootView.findViewById(R.id.imageViewFragmentAvatar);
        radioGroupOS = rootView.findViewById(R.id.radioGroupFragmentOS);
        textViewMood = rootView.findViewById(R.id.textViewFragmentMood);
        seekBarMood = rootView.findViewById(R.id.seekBarFragmentMood);
        imageViewMood = rootView.findViewById(R.id.imageViewFragmentMood);
        buttonSubmit = rootView.findViewById(R.id.buttonFragmentSubmit);

        buttonSubmit.setOnClickListener(v -> {
            int selectedRadioButton = radioGroupOS.getCheckedRadioButtonId();
            int selectedMood = seekBarMood.getProgress();
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            boolean emailGood = checkEmail(email);
            boolean nameGood = !name.isEmpty();
            boolean OSGood = selectedRadioButton != -1;
            if (!emailGood)
                Toast.makeText(getActivity(), "Invalid Email",
                        Toast.LENGTH_SHORT).show();
            if (!nameGood)
                Toast.makeText(getActivity(), "Invalid Name",
                        Toast.LENGTH_SHORT).show();
            if (!OSGood)
                Toast.makeText(getActivity(), "Must Select An OS",
                        Toast.LENGTH_SHORT).show();
            if (avatarNumber == 0)
                Toast.makeText(getActivity(), "Must Select An Avatar Image",
                        Toast.LENGTH_SHORT).show();
            if (emailGood && nameGood && OSGood && avatarNumber != 0) {
                // bring the user to the display activity
                Profile profile = new Profile(name, email, avatarNumber, selectedRadioButton, selectedMood);
                sendData.fromMainFragment(true, false, profile);
            }
        });

        imageViewAvatar.setOnClickListener(v -> {
            // send data to the main activity telling it to bring up the select avatar fragment
            sendData.fromMainFragment(false, true, null);
        });

        seekBarMood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int seekBarValue = seekBarMood.getProgress();
                String moodString = "Your Current Mood: ";
                switch (seekBarValue) {
                    case 0: imageViewMood.setImageResource(R.drawable.angry);
                        moodString = moodString + "Angry";
                        break;
                    case 1: imageViewMood.setImageResource(R.drawable.sad);
                        moodString = moodString + "Sad";
                        break;
                    case 2: imageViewMood.setImageResource(R.drawable.happy);
                        moodString = moodString + "Happy";
                        break;
                    case 3: imageViewMood.setImageResource(R.drawable.awesome);
                        moodString = moodString + "Awesome";
                        break;
                }
                textViewMood.setText(moodString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (avatarNumber) {
            case 0: imageViewAvatar.setImageResource(R.drawable.select_avatar);
                break;
            case 1: imageViewAvatar.setImageResource(R.drawable.avatar_f_1);
                break;
            case 2: imageViewAvatar.setImageResource(R.drawable.avatar_f_2);
                break;
            case 3: imageViewAvatar.setImageResource(R.drawable.avatar_f_3);
                break;
            case 4: imageViewAvatar.setImageResource(R.drawable.avatar_m_1);
                break;
            case 5: imageViewAvatar.setImageResource(R.drawable.avatar_m_2);
                break;
            case 6: imageViewAvatar.setImageResource(R.drawable.avatar_m_3);
                break;
        }
    }

    // return true if the inputted name is in the format abc@def.xyz
    private boolean checkEmail(String email) {
        boolean containsAt = email.contains("@");
        boolean containsDot = email.contains(".") && email.lastIndexOf(".") > email.indexOf("@");
        boolean containsSpace = email.contains(" ");
        String address;
        String server;
        String suffix;
        if (containsDot && containsAt && !containsSpace) {
            address = email.substring(0, email.lastIndexOf('@'));
            server = email.substring(email.indexOf('@') + 1, email.lastIndexOf("."));
            suffix = email.substring(email.lastIndexOf(".") + 1);
            return !address.isEmpty() && !server.isEmpty() && !suffix.isEmpty() && !address.contains("@");
        }
        return false;
    }

    public void updateValues(int avatarNumber) {
        this.avatarNumber = avatarNumber;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromMainToActivity) {
            sendData = (IFromMainToActivity) context;
        } else {
            throw new RuntimeException(context + " must implement IFromMainToActivity");
        }
    }

    public interface IFromMainToActivity {
        void fromMainFragment(boolean submitClick, boolean avatarClick, Profile profile);
    }
}