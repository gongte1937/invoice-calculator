import { TextField, MenuItem } from "@mui/material";
import { Control, Controller, FieldError } from "react-hook-form";

// Static currency list (can be extended later)
export const currencies = ["NZD", "USD", "AUD", "EUR", "GBP", "JPY"];

interface CurrencySelectorProps {
  name: string;
  label: string;
  control: Control<any>;
  error?: FieldError;
}

export default function CurrencySelector({
  name,
  label,
  control,
  error,
}: CurrencySelectorProps) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <TextField
          select
          label={label}
          {...field}
          error={!!error}
          helperText={error?.message}
        >
          {currencies.map((currency) => (
            <MenuItem key={currency} value={currency}>
              {currency}
            </MenuItem>
          ))}
        </TextField>
      )}
    />
  );
}
