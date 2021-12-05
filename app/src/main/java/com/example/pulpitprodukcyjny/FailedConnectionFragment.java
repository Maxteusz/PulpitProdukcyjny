package com.example.pulpitprodukcyjny;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;


public class FailedConnectionFragment extends Fragment {
    TextInputEditText ipAdressConnection, portConnection;
    MaterialButton setButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.failed_connection_fragment, container, false);



    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ipAdressConnection = view.findViewById(R.id.editText_ipAdress);
        portConnection = view.findViewById(R.id.editText_port);
        setButton = view.findViewById(R.id.setButton);
        if(!MainActivity.connectionIpAdress.equals(""))
            ipAdressConnection.setText(MainActivity.connectionIpAdress);
        if(!MainActivity.connectionPort.equals(""))
            portConnection.setText(MainActivity.connectionPort);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.connectionIpAdress = ipAdressConnection.getText().toString();
                MainActivity.connectionPort = portConnection.getText().toString();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out)
                        .replace(R.id.your_placeholder, new LogoFragment())
                        .commit();
            }
        });




    }
}
