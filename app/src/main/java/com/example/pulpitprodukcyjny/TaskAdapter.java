package com.example.pulpitprodukcyjny;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

import static com.example.pulpitprodukcyjny.R.drawable.linear;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    Context context;
    ArrayList<Order> orderArrayList;
    int heightCard;
    RecyclerView recyclerView;


    public TaskAdapter(Context context, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        heightCard = 0;


    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_layout, parent, false);
        return new TaskAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {

        Order currentOrder = orderArrayList.get(position);
        holder.overallProgressBar.setProgress(0);
        if (currentOrder.getResourcesSize() == 0) {
            holder.overallProgressBar.setProgress(0);
            holder.overallResult.setText("0%");
        }
        else
        {
            holder.overallProgressBar.setProgress(currentOrder.getOverallResult());
            holder.overallResult.setText(currentOrder.getOverallResult() + "%");
        }
        holder.overallProgressBar.setProgress(currentOrder.getOverallResult());
        holder.number_textView.setText(currentOrder.getNumber() + "");
        holder.customer_texView.setText(currentOrder.getCustomer()+"");

        if (holder.customer_texView.getText().equals(""))
            holder.customer_texView.setVisibility(View.GONE);
        holder.product_textView.setText(currentOrder.getProduct() + " (" + currentOrder.getProductCatalogNumber() + ")");
        holder.orderData_textView.setText("Data Wysyłki: " + currentOrder.getShipDate() + "");

        if (currentOrder.isInProgress())
            holder.gifImageView.setVisibility(View.VISIBLE);

        if (currentOrder.getShipDate().equals(""))
            holder.orderData_textView.setVisibility(View.GONE);

        if (!currentOrder.getSpecialPole1().equals("")) {
            addTextView(holder.linearLayout, MainActivity.headerSpecialPole1 + ":", 14, Color.GRAY, 1, 10, false, currentOrder , 0);
            addTextView(holder.linearLayout, currentOrder.getSpecialPole1(), 16, Color.RED, 1 , 0, false, currentOrder , 0);
        }
        if (!currentOrder.getSpecialPole2().equals("")) {
            addTextView(holder.linearLayout, MainActivity.headerSpecialPole2 + ":", 14, Color.GRAY, 1,20, false, currentOrder , 0);
            addTextView(holder.linearLayout, currentOrder.getSpecialPole2(), 16, Color.RED, 1,0, false, currentOrder , 0);
        }
        if (!currentOrder.getSpecialPole3().equals("")) {
            addTextView(holder.linearLayout, MainActivity.headerSpecialPole3 + ":", 14, Color.GRAY, 1,20, false, currentOrder , 0);
            addTextView(holder.linearLayout, currentOrder.getSpecialPole3(), 16, Color.RED, 1,0, false, currentOrder , 0);
        }
        holder.materialCardView_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.more_information_layout.getVisibility() == View.GONE) {
                    holder.more_information_layout.setVisibility(View.VISIBLE);
                    holder.tRollup.setText("Zwiń");
                }
                else if (holder.more_information_layout.getVisibility() == View.VISIBLE) {
                    holder.more_information_layout.setVisibility(View.GONE);
                    holder.tRollup.setText("Rozwiń");
                }
            }
        });

        for (int i = 0; i < currentOrder.getResourcesSize(); i++)
            addProgressBar(holder.linearLayout, currentOrder.getIndexOfArrarResources(i).getPlannedQuantity(), currentOrder.getIndexOfArrarResources(i).getRealizedQuantity(),
                    currentOrder.getIndexOfArrarResources(i).getName() + " (" + currentOrder.getIndexOfArrarResources(i).realizedInPercentage() + "%" + ")",i, currentOrder);


    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }





    public void addTextView(LinearLayout linearLayout, String text, int textSize, int color, int style, int mariginTop, boolean isClicable, Order order, int indexOfResource) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(textSize);
        if (style == 1)
            textView.setTypeface(null, Typeface.BOLD);
        if(isClicable) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Bundle bundle = new Bundle();
                    bundle.putSerializable("resource", order.getIndexOfArrarResources(indexOfResource));

                    AppCompatActivity activity = (AppCompatActivity) textView.getContext();
                    Fragment myFragment = new ResourceDetailsFragment();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out)
                            .replace(R.id.your_placeholder, myFragment).addToBackStack(null).commit();
                   myFragment.setArguments(bundle);
                    Log.i ("Click", order.getIndexOfArrarResources(indexOfResource).getName()+"");

                }
            });
        }


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, mariginTop, 0, 0);
        textView.setLayoutParams(params);
        ((LinearLayout) linearLayout).addView(textView);
    }

    public void addProgressBar(LinearLayout linearLayout, int max, int progress, String nameResource, int indexOfResource, Order order) {
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 30);
        params.setMargins(20, 0, 100, 10);
        progressBar.setProgressDrawable(ResourcesCompat.getDrawable(context.getResources(), linear, context.getTheme()));
        progressBar.setLayoutParams(params);
        progressBar.setProgress(progress);
        progressBar.setMax(max);
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        addTextView(linearLayout, nameResource, 14, Color.DKGRAY, 1,20,true, order, indexOfResource);
        ((LinearLayout) linearLayout).addView(progressBar);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView number_textView, customer_texView, product_textView, orderData_textView, overallResult, tRollup;
        MaterialCardView more_information_layout;
        MaterialCardView materialCardView_button;
        LinearLayout linearLayout;
        LinearLayout lin3;
        ProgressBar overallProgressBar;
        GifImageView gifImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number_textView = itemView.findViewById(R.id.number_textview);
            tRollup = itemView.findViewById(R.id.tRollup);
            customer_texView = itemView.findViewById(R.id.customer_textView);
            product_textView = itemView.findViewById(R.id.product_textView);
            orderData_textView = itemView.findViewById(R.id.orderData_textView);
            more_information_layout = itemView.findViewById(R.id.more_information_card);
            materialCardView_button = itemView.findViewById(R.id.open_button);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            lin3 = itemView.findViewById(R.id.lin33);
            gifImageView = itemView.findViewById(R.id.GifImageView);
            overallResult = itemView.findViewById(R.id.text_progress_bar);
            overallProgressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
