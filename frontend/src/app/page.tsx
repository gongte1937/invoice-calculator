"use client";

import InvoiceForm from "@/components/InvoiceForm";
import { Container, Typography } from "@mui/material";

export default function HomePage() {
  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Multi-Currency Invoice
      </Typography>
      <InvoiceForm />
    </Container>
  );
}
