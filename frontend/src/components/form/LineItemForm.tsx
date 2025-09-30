import {
  Stack,
  TextField,
  IconButton,
  Paper,
  Typography,
  Box,
} from "@mui/material";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";
import { Add as AddIcon, Delete as DeleteIcon } from "@mui/icons-material";
import CurrencySelector from "./CurrencySelector";

interface LineItemData {
  description: string;
  amount: number | null;
  currency: string;
}

interface LineItemFormProps {
  control: Control<any>;
  errors?: any;
}

export default function LineItemForm({ control, errors }: LineItemFormProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: "lines",
  });

  const addLineItem = () => {
    append({ description: "", amount: null, currency: "USD" });
  };

  const removeLineItem = (index: number) => {
    if (fields.length > 1) {
      remove(index);
    }
  };

  return (
    <Stack spacing={2}>
      <Typography variant="h6">Line Items</Typography>

      {fields.map((field, index) => (
        <Paper key={field.id} sx={{ p: 2, position: "relative" }}>
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 2,
            }}
          >
            <Typography variant="subtitle1">Line Item {index + 1}</Typography>
            {fields.length > 1 && (
              <IconButton
                onClick={() => removeLineItem(index)}
                color="error"
                size="small"
              >
                <DeleteIcon />
              </IconButton>
            )}
          </Box>

          <Stack spacing={2}>
            <Controller
              name={`lines.${index}.description`}
              control={control}
              render={({ field }) => (
                <TextField
                  label="Description"
                  {...field}
                  error={!!errors?.lines?.[index]?.description}
                  helperText={errors?.lines?.[index]?.description?.message}
                />
              )}
            />

            <Controller
              name={`lines.${index}.amount`}
              control={control}
              render={({ field }) => (
                <TextField
                  label="Amount"
                  type="number"
                  {...field}
                  value={field.value ?? ""}
                  onChange={(e) => {
                    const value = e.target.value;
                    field.onChange(value === "" ? undefined : Number(value));
                  }}
                  error={!!errors?.lines?.[index]?.amount}
                  helperText={errors?.lines?.[index]?.amount?.message}
                />
              )}
            />

            <CurrencySelector
              name={`lines.${index}.currency`}
              label="Line Currency"
              control={control}
              error={errors?.lines?.[index]?.currency}
            />
          </Stack>
        </Paper>
      ))}

      <IconButton
        onClick={addLineItem}
        color="primary"
        sx={{ alignSelf: "flex-start" }}
      >
        <AddIcon /> Add Line Item
      </IconButton>
    </Stack>
  );
}
