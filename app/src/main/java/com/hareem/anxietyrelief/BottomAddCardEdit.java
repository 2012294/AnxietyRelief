package com.hareem.anxietyrelief;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class BottomAddCardEdit  extends BottomSheetDialogFragment {

        private Context activityContext;
    private String cardNumber;



    private String cardname;

    private String cvv;
    private View clickedView;

    private String month;


    private String year;

    private CardData cardData; // Store the CardData associated with the clicked view

    // Constructor to accept the clicked view and associated CardData
    public BottomAddCardEdit(View clickedView, CardData cardData, String cardNumber, String cardname, String cvv, String month, String year) {
        this.clickedView = clickedView;
        this.cardData = cardData;
        this.cardNumber = cardNumber;
        this.cardname = cardname;
        this.cvv = cvv;
        this.month = month;
        this.year = year;
    }

    // Method to remove the clicked view and its associated CardData from the list
    public interface EditListener {
        void onEditComplete(CardData cardData);
    }

    // Other members and methods...

    // Store reference to the listener
    private EditListener editListener;

    // Method to set the listener
    public void setEditListener(EditListener editListener) {
        this.editListener = editListener;
    }

    // Method to remove the clicked view and its associated CardData from the list
    private void removeClickedViewAndData() {
        if (editListener != null) {
            editListener.onEditComplete(cardData);
        }

        if (clickedView != null && clickedView.getParent() != null) {
            ((ViewGroup) clickedView.getParent()).removeView(clickedView);
        }
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    // ...
        TextInputEditText cardNumberEditText,cardnameEditText,monthEditText,yearEditText,cvvEditText;
        TextInputLayout cardNumberInputLayout,cardnameInputLayout,monthInputLayout,yearInputLayout,cvvInputLayout;




        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            activityContext = context;
        }




        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_bottom_add_card, container, false);

            // Find the "Done" button by its ID
            Button doneButton = view.findViewById(R.id.btndone);
            cardNumberEditText=view.findViewById(R.id.cardNumberEditText);
            cardNumberInputLayout=view.findViewById(R.id.cardNumberInputLayout);
            cardnameInputLayout=view.findViewById(R.id.cardnameInputLayout);
            cardnameEditText=view.findViewById(R.id.cardnameEditText);
            monthInputLayout=view.findViewById(R.id.monthInputLayout);
            monthEditText=view.findViewById(R.id.monthEditText);
            yearEditText=view.findViewById(R.id.yearEditText);
            yearInputLayout=view.findViewById(R.id.yearInputLayout);
            cvvEditText=view.findViewById(R.id.cvvEditText);
            cvvInputLayout=view.findViewById(R.id.cvvInputLayout);

             cardNumberEditText.setText(getCardNumber());
             cardnameEditText.setText(getCardname());
             cvvEditText.setText(getCvv());
             monthEditText.setText(getMonth());
             yearEditText.setText(getYear());

            OnTypeErrorRemoval();


            // Set an OnClickListener for the "Done" button
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cardNumber = cardNumberEditText.getText().toString();
                    String cardname=cardnameEditText.getText().toString();
                    String cvv=cvvEditText.getText().toString();
                    String month=monthEditText.getText().toString();
                    String year=yearEditText.getText().toString();
                    if (TextUtils.isEmpty(cardNumber)) {
                        cardNumberInputLayout.setError("Card Number is Required");
                        cardNumberInputLayout.setErrorIconDrawable(null);
                    }  else if (TextUtils.isEmpty(month)) {
                        monthInputLayout.setError("Card Number is Required");
                        monthInputLayout.setErrorIconDrawable(null);
                    } else if (TextUtils.isEmpty(year)) {
                        yearInputLayout.setError("Card Number is Required");
                        yearInputLayout.setErrorIconDrawable(null);
                    } else if (TextUtils.isEmpty(cardname)) {
                        cardnameInputLayout.setError("Card Number is Required");
                        cardnameInputLayout.setErrorIconDrawable(null);

                    } else if (TextUtils.isEmpty(cvv)) {
                        cvvInputLayout.setError("Card Number is Required");
                        cvvInputLayout.setErrorIconDrawable(null);
                    }else if (cardNumber.length()!=19) {
                        cardNumberInputLayout.setError("enter full card number");
                        cardNumberInputLayout.setErrorIconDrawable(null);
                    }else if (month.length()!=2) {
                        monthInputLayout.setError("enter month in XX format");
                        monthInputLayout.setErrorIconDrawable(null);
                    }else if (year.length()!=4) {
                        yearInputLayout.setError("enter year in XXXX format");
                        yearInputLayout.setErrorIconDrawable(null);
                    }else if (cvv.length()!=3) {
                        cvvInputLayout.setError("enter full cvv");
                        cvvInputLayout.setErrorIconDrawable(null);
                    }else if (!isValidMonth(month)) {
                        monthInputLayout.setError("Invalid month");
                        monthInputLayout.setErrorIconDrawable(null);
                    } else if (!isValidYear(year)) {
                        yearInputLayout.setError("invalid year");
                        yearInputLayout.setErrorIconDrawable(null);
                    } else{

                        CardData cardData=new CardData(cardNumber,cardname,cvv,month,year);

                        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);

                        if (getActivity() instanceof Patient_payment_setting) {
                            ((Patient_payment_setting) getActivity()).updateCardNumber(lastFourDigits,cardData);
                        }
                        dismiss();
                        removeClickedViewAndData();
                    }



                }
            });

            return view;
        }




        private boolean isValidMonth(String month) {
            try {
                int monthValue = Integer.parseInt(month);
                return monthValue >= 1 && monthValue <= 12;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        private boolean isValidYear(String year) {
            try {
                int yearValue = Integer.parseInt(year);
                Calendar calendar = Calendar.getInstance();

                int currentYear =  calendar.get(Calendar.YEAR);;
                return yearValue >= currentYear;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            final TextInputEditText cardNumberEditText = view.findViewById(R.id.cardNumberEditText);

            // Create a custom TextWatcher
            cardNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString().replaceAll("[^\\d]", ""); // Remove non-numeric characters
                    StringBuilder formattedText = new StringBuilder();

                    for (int i = 0; i < text.length(); i++) {
                        formattedText.append(text.charAt(i));
                        if ((i + 1) % 4 == 0 && (i + 1) < text.length()) {
                            formattedText.append("-");
                        }
                    }

                    cardNumberEditText.removeTextChangedListener(this); // Prevent infinite loop
                    cardNumberEditText.setText(formattedText.toString());
                    cardNumberEditText.setSelection(cardNumberEditText.getText().length()); // Set cursor to the end
                    cardNumberEditText.addTextChangedListener(this); // Add TextWatcher back
                }
            });
        }
        private void OnTypeErrorRemoval() {
            cardNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    cardNumberInputLayout.setError(null);
                    cardNumberInputLayout.setErrorEnabled(false);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            cardnameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Clear the error when the user starts typing
                    cardnameInputLayout.setError(null);
                    cardnameInputLayout.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            monthEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Clear the error when the user starts typing
                    monthInputLayout.setError(null);
                    monthInputLayout.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            yearEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Clear the error when the user starts typing
                    yearInputLayout.setError(null);
                    yearInputLayout.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            cvvEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Clear the error when the user starts typing
                    cvvInputLayout.setError(null);
                    cvvInputLayout.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }


}
