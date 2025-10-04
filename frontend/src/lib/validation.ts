import { z } from "zod";
import { CURRENCIES_ENUM } from "@/constants";

// Invoice line item validation schema
export const InvoiceLineSchema = z.object({
  description: z.string().min(1, "Description is required"),
  amount: z
    .number()
    .positive("Must be > 0")
    .nullable()
    .refine((val) => val !== null, "Amount is required"),
  currency: z.enum(CURRENCIES_ENUM),
});

// Main invoice validation schema
export const InvoiceSchema = z.object({
  date: z.date().refine((d) => d >= new Date("1999-01-04"), {
    message: "Date must be after 1999-01-04",
  }),
  currency: z.enum(CURRENCIES_ENUM),
  lines: z
    .array(InvoiceLineSchema)
    .min(1, "At least one line item is required"),
});

// Export inferred types
export type InvoiceLineData = z.infer<typeof InvoiceLineSchema>;
export type InvoiceFormData = z.infer<typeof InvoiceSchema>;
