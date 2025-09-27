import { Stack, TextField } from "@mui/material";
import { Control, Controller, FieldErrors } from "react-hook-form";
import CurrencySelector from "./CurrencySelector";

interface LineItemData {
  description: string;
  amount: number;
  currency: string;
}

interface LineItemFormProps {
  control: Control<any>;
  errors?: FieldErrors<LineItemData>;
  namePrefix?: string;
}

export default function LineItemForm({
  control,
  errors,
  namePrefix = "line",
}: LineItemFormProps) {
  return (
    <Stack spacing={2}>
      <Controller
        name={`${namePrefix}.description`}
        control={control}
        render={({ field }) => (
          <TextField
            label="Description"
            {...field}
            error={!!errors?.description}
            helperText={errors?.description?.message}
          />
        )}
      />

      <Controller
        name={`${namePrefix}.amount`}
        control={control}
        render={({ field }) => (
          <TextField
            label="Amount"
            type="number"
            {...field}
            onChange={(e) => field.onChange(Number(e.target.value))}
            error={!!errors?.amount}
            helperText={errors?.amount?.message}
          />
        )}
      />

      <CurrencySelector
        name={`${namePrefix}.currency`}
        label="Line Currency"
        control={control}
        error={errors?.currency}
      />
    </Stack>
  );
}
