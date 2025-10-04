import { TextField, MenuItem } from "@mui/material";
import { Control, Controller, FieldError } from "react-hook-form";
import { CURRENCIES } from "@/constants";

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
          {CURRENCIES.map((currency) => (
            <MenuItem key={currency} value={currency}>
              {currency}
            </MenuItem>
          ))}
        </TextField>
      )}
    />
  );
}
