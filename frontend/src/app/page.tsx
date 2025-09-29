"use client";

import { InvoiceFormData } from "@/components/form/InvoiceFormWrapper";
import InvoiceForm from "@/components/InvoiceForm";
import { Container, Typography } from "@mui/material";
import { useState } from "react";
import { postInvoiceTotal } from "./api";

export default function HomePage() {
  const [total, setTotal] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const handleSubmit = async (data: InvoiceFormData) => {
    try {
      setError(null);
      setTotal(null);
      setLoading(true);

      const result = await postInvoiceTotal(data);
      setTotal(result);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : "Invoice calculation error";
      setError(errorMessage);
      console.error("Invoice calculation error:", err);
    } finally {
      setLoading(false);
    }
  };
  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Multi-Currency Invoice
      </Typography>
      <InvoiceForm
        onSubmit={handleSubmit}
        total={total}
        error={error}
        loading={loading}
      />
    </Container>
  );
}
