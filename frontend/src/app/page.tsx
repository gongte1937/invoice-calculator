// src/app/page.tsx
"use client";

import { Container, Typography, Paper, Button } from "@mui/material";

export default function HomePage() {
  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Multi-Currency Invoice
        </Typography>
        <Typography variant="body1" gutterBottom>
          This will be our invoice calculator form.
        </Typography>
        <Button variant="contained" color="primary">
          Placeholder Button
        </Button>
      </Paper>
    </Container>
  );
}
