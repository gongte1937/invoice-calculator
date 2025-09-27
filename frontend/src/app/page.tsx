"use client";

import { InvoiceFormData } from "@/components/form/InvoiceFormWrapper";
import InvoiceForm from "@/components/InvoiceForm";
import { Container, Typography } from "@mui/material";

export default function HomePage() {
  const handleSubmit = (data: InvoiceFormData) => {
    console.log(data);
  };
  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Multi-Currency Invoice
      </Typography>
      <InvoiceForm onSubmit={handleSubmit} />
    </Container>
  );
}
