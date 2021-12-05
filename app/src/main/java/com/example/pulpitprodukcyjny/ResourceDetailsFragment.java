package com.example.pulpitprodukcyjny;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ResourceDetailsFragment extends Fragment {

    Resource resource;
    TextView tPlannedTime, tEstimatedTime, tPlannedQuantity, tRealizedQuantity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resource = (Resource) getArguments().get("resource");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.resource_details, parent, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tPlannedTime = view.findViewById(R.id.plannedTime_textView);
        tEstimatedTime = view.findViewById(R.id.estimatedTime_TextView);
        tPlannedQuantity = view.findViewById(R.id.plannedQuantity_textView);
        tRealizedQuantity = view.findViewById(R.id.realizedQuantity_TextView);
        tPlannedQuantity.setText(resource.getPlannedQuantity()+"");
        tRealizedQuantity.setText(resource.getRealizedQuantity()+"");
        tPlannedTime.setText((int) resource.getPlannedTime() + " min");
        tEstimatedTime.setText(resource.getEstamitedTime() + " min");

    }
}




