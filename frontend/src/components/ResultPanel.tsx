"use client";

import { Alert, Paper, Stack, Typography } from "@mui/material";

type Props = {
  total?: string | null;
  baseCurrency: string;
  error?: string | null;
};

export default function ResultPanel({ total, baseCurrency, error }: Props) {
  if (error) {
    return (
      <Paper
        elevation={8}
        sx={{
          p: 4,
          height: 200,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          border: "2px solid #f44336",
          borderRadius: 2,
        }}
      >
        <Alert severity="error" sx={{ fontSize: "1.1rem" }}>
          {error}
        </Alert>
      </Paper>
    );
  }

  if (total) {
    return (
      <Paper
        elevation={8}
        sx={{
          p: 4,
          height: 200,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          border: "2px solid #4caf50",
          borderRadius: 2,
          background: "linear-gradient(135deg, #f5f5f5 0%, #ffffff 100%)",
          marginBottom: 4,
        }}
      >
        <Stack spacing={2} alignItems="center">
          <Typography variant="h4" fontWeight="bold" color="text.primary">
            Invoice Total
          </Typography>
          <Typography
            variant="h3"
            component="h5"
            sx={{
              color: "#f44336",
              fontWeight: "bold",
              textShadow: "1px 1px 2px rgba(0,0,0,0.1)",
            }}
          >
            {total} {baseCurrency}
          </Typography>
        </Stack>
      </Paper>
    );
  }

  return null;
}
