"use client";

import {
  Alert,
  CircularProgress,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

type Props = {
  total?: string | null;
  baseCurrency: string;
  error?: string | null;
  loading?: boolean;
};

const basePaperStyle = {
  p: 4,
  height: 200,
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  borderRadius: 2,
  marginBottom: 4,
  border: "2px solid #2196f3",
  background: "linear-gradient(135deg, #e3f2fd 0%, #ffffff 100%)",
};

export default function ResultPanel({
  total,
  baseCurrency,
  error,
  loading,
}: Props) {
  if (error) {
    return (
      <Alert severity="error" sx={{ fontSize: "1.1rem", marginBottom: 4 }}>
        {error}
      </Alert>
    );
  }

  if (loading) {
    return (
      <Paper
        elevation={8}
        sx={{
          ...basePaperStyle,
        }}
      >
        <Stack spacing={3} alignItems="center">
          <CircularProgress size={60} thickness={4} />
          <Typography variant="h6" color="text.secondary">
            Calculating invoice total...
          </Typography>
        </Stack>
      </Paper>
    );
  }

  if (total) {
    return (
      <Paper
        elevation={8}
        sx={{
          ...basePaperStyle,
        }}
      >
        <Stack spacing={2} alignItems="center">
          <Typography variant="h4" fontWeight="bold">
            Invoice Total
          </Typography>
          <Typography
            variant="h3"
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
