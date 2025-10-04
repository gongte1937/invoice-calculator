"use client";

import { Button, Stack, Typography, Paper } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";

import DatePickerField from "./form/DatePickerField";
import CurrencySelector from "./form/CurrencySelector";
import LineItemForm from "./form/LineItemForm";
import ResultPanel from "./ResultPanel";
import { useEffect, useState } from "react";
import { DEFAULT_BASE_CURRENCY } from "@/constants";
import { InvoiceFormData } from "@/lib/validation";
import InvoiceFormWrapper from "./form/InvoiceFormWrapper";

interface InvoiceFormProps {
  onSubmit?: (data: InvoiceFormData) => void;
  defaultValues?: Partial<InvoiceFormData>;
  total?: string | null;
  error?: string | null;
  loading?: boolean;
}

export default function InvoiceForm({
  onSubmit,
  defaultValues,
  total,
  error,
  loading,
}: InvoiceFormProps) {
  const [lastBaseCurrency, setLastBaseCurrency] = useState(
    defaultValues?.currency ?? DEFAULT_BASE_CURRENCY
  );

  useEffect(() => {
    setLastBaseCurrency(defaultValues?.currency ?? DEFAULT_BASE_CURRENCY);
  }, [defaultValues?.currency]);

  const handleFormSubmit = (data: InvoiceFormData) => {
    setLastBaseCurrency(data.currency);
    if (onSubmit) {
      onSubmit(data);
    }
  };
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Paper sx={{ p: 4, mt: 4 }}>
        <Typography variant="h5" gutterBottom>
          Invoice Form
        </Typography>

        <InvoiceFormWrapper
          onSubmit={handleFormSubmit}
          defaultValues={defaultValues}
        >
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

      <ResultPanel
        baseCurrency={lastBaseCurrency}
        total={total}
        error={error}
        loading={loading}
      />
    </LocalizationProvider>
  );
}
