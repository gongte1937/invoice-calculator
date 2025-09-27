import { Control, Controller, FieldError } from "react-hook-form";
import { DatePicker } from "@mui/x-date-pickers";

interface DatePickerFieldProps {
  name: string;
  label: string;
  control: Control<any>;
  error?: FieldError;
}

export default function DatePickerField({
  name,
  label,
  control,
  error,
}: DatePickerFieldProps) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <DatePicker
          label={label}
          value={field.value}
          onChange={(date) => field.onChange(date)}
          slotProps={{
            textField: {
              error: !!error,
              helperText: error?.message,
            },
          }}
        />
      )}
    />
  );
}
