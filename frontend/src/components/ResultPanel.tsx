"use client";

import { Alert, Chip, Paper, Stack, Typography } from "@mui/material";

type Props = {
  total?: string | null;
  baseCurrency: string;
  error?: string | null;
};

export default function ResultPanel({ total, baseCurrency, error }: Props) {
  if (error) {
    return (
      <Paper sx={{ p: 3 }}>
        <Alert severity="error">{error}</Alert>
      </Paper>
    );
  }

  if (total) {
    return (
      <Paper sx={{ p: 3 }}>
        <Stack direction="row" spacing={2} alignItems="center">
          <Typography variant="h6">Invoice Total</Typography>
          <Chip label={`${total} ${baseCurrency}`} />
        </Stack>
      </Paper>
    );
  }

  return null;
}
