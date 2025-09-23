"use client";

import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

import {
  Button,
  TextField,
  MenuItem,
  Stack,
  Typography,
  Paper,
} from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";

// Static currency list (can be extended later)
const currencies = ["NZD", "USD", "AUD", "EUR", "GBP", "JPY"];

// Zod validation schema
const InvoiceLineSchema = z.object({
  description: z.string().min(1, "Required"),
  amount: z.number().positive("Must be > 0"),
  currency: z.enum(currencies as [string, ...string[]]),
});

const InvoiceSchema = z.object({
  date: z.date().refine((d) => d >= new Date("1999-01-04"), {
    message: "Date must be after 1999-01-04",
  }),
  currency: z.enum(currencies as [string, ...string[]]),
  line: InvoiceLineSchema,
});

type InvoiceFormData = z.infer<typeof InvoiceSchema>;

export default function InvoiceForm() {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<InvoiceFormData>({
    resolver: zodResolver(InvoiceSchema),
    defaultValues: {
      date: new Date(),
      currency: "NZD",
      line: { description: "", amount: 0, currency: "USD" },
    },
  });

  const onSubmit = (data: InvoiceFormData) => {
    console.log("Form submit:", data);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Paper sx={{ p: 4, mt: 4 }}>
        <Typography variant="h5" gutterBottom>
          Invoice Form
        </Typography>
        <form onSubmit={handleSubmit(onSubmit)}>
          <Stack spacing={3}>
            {/* Invoice Date */}
            <Controller
              name="date"
              control={control}
              render={({ field }) => (
                <DatePicker
                  label="Invoice Date"
                  value={field.value}
                  onChange={(date) => field.onChange(date)}
                  slotProps={{
                    textField: {
                      error: !!errors.date,
                      helperText: errors.date?.message,
                    },
                  }}
                />
              )}
            />

            {/* Base Currency */}
            <Controller
              name="currency"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Base Currency"
                  {...field}
                  error={!!errors.currency}
                  helperText={errors.currency?.message}
                >
                  {currencies.map((c) => (
                    <MenuItem key={c} value={c}>
                      {c}
                    </MenuItem>
                  ))}
                </TextField>
              )}
            />

            {/* Single Line Item */}
            <Controller
              name="line.description"
              control={control}
              render={({ field }) => (
                <TextField
                  label="Description"
                  {...field}
                  error={!!errors.line?.description}
                  helperText={errors.line?.description?.message}
                />
              )}
            />
            <Controller
              name="line.amount"
              control={control}
              render={({ field }) => (
                <TextField
                  label="Amount"
                  type="number"
                  {...field}
                  onChange={(e) => field.onChange(Number(e.target.value))}
                  error={!!errors.line?.amount}
                  helperText={errors.line?.amount?.message}
                />
              )}
            />
            <Controller
              name="line.currency"
              control={control}
              render={({ field }) => (
                <TextField
                  select
                  label="Line Currency"
                  {...field}
                  error={!!errors.line?.currency}
                  helperText={errors.line?.currency?.message}
                >
                  {currencies.map((c) => (
                    <MenuItem key={c} value={c}>
                      {c}
                    </MenuItem>
                  ))}
                </TextField>
              )}
            />

            <Button type="submit" variant="contained">
              Calculate Total
            </Button>
          </Stack>
        </form>
      </Paper>
    </LocalizationProvider>
  );
}
