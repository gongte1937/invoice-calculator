"use client";

import { Button, Stack, Typography, Paper } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";

import InvoiceFormWrapper, { InvoiceFormData } from "./form/InvoiceFormWrapper";
import DatePickerField from "./form/DatePickerField";
import CurrencySelector from "./form/CurrencySelector";
import LineItemForm from "./form/LineItemForm";
import ResultPanel from "./ResultPanel";

interface InvoiceFormProps {
  onSubmit?: (data: InvoiceFormData) => void;
  defaultValues?: Partial<InvoiceFormData>;
}

export default function InvoiceForm({
  onSubmit,
  defaultValues,
}: InvoiceFormProps) {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Paper sx={{ p: 4, mt: 4 }}>
        <Typography variant="h5" gutterBottom>
          Invoice Form
        </Typography>

        <InvoiceFormWrapper onSubmit={onSubmit} defaultValues={defaultValues}>
          {({ control, handleSubmit, errors }) => (
            <form onSubmit={handleSubmit}>
              <Stack spacing={3}>
                {/* Invoice Date */}
                <DatePickerField
                  name="date"
                  label="Invoice Date"
                  control={control}
                  error={errors.date}
                />

                {/* Base Currency */}
                <CurrencySelector
                  name="currency"
                  label="Base Currency"
                  control={control}
                  error={errors.currency}
                />

                {/* Line Items */}
                <LineItemForm control={control} errors={errors} />

                <Button type="submit" variant="contained">
                  Calculate Total
                </Button>
              </Stack>
            </form>
          )}
        </InvoiceFormWrapper>
      </Paper>

      <ResultPanel baseCurrency="NZD" />
    </LocalizationProvider>
  );
}
