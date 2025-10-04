import { z } from "zod";
import { CURRENCIES } from "@/app/constants";

// Import the validation schemas from InvoiceFormWrapper
// We'll recreate them here for testing since they're not exported
const CURRENCIES_ENUM = CURRENCIES as readonly [string, ...string[]];

const InvoiceLineSchema = z.object({
  description: z.string().min(1, "Description is required"),
  amount: z
    .number()
    .positive("Must be > 0")
    .nullable()
    .refine((val) => val !== null, "Amount is required"),
  currency: z.enum(CURRENCIES_ENUM),
});

const InvoiceSchema = z.object({
  date: z.date().refine((d) => d >= new Date("1999-01-04"), {
    message: "Date must be after 1999-01-04",
  }),
  currency: z.enum(CURRENCIES_ENUM),
  lines: z
    .array(InvoiceLineSchema)
    .min(1, "At least one line item is required"),
});

describe("Invoice Form Validation", () => {
  describe("InvoiceLineSchema", () => {
    it("validates a correct line item", () => {
      const validLine = {
        description: "Test item",
        amount: 100.5,
        currency: "USD",
      };

      const result = InvoiceLineSchema.safeParse(validLine);
      expect(result.success).toBe(true);
    });

    it("rejects empty description", () => {
      const invalidLine = {
        description: "",
        amount: 100,
        currency: "USD",
      };

      const result = InvoiceLineSchema.safeParse(invalidLine);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe("Description is required");
      }
    });

    it("rejects null amount", () => {
      const invalidLine = {
        description: "Test item",
        amount: null,
        currency: "USD",
      };

      const result = InvoiceLineSchema.safeParse(invalidLine);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe("Amount is required");
      }
    });

    it("rejects negative amount", () => {
      const invalidLine = {
        description: "Test item",
        amount: -10,
        currency: "USD",
      };

      const result = InvoiceLineSchema.safeParse(invalidLine);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe("Must be > 0");
      }
    });

    it("rejects zero amount", () => {
      const invalidLine = {
        description: "Test item",
        amount: 0,
        currency: "USD",
      };

      const result = InvoiceLineSchema.safeParse(invalidLine);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe("Must be > 0");
      }
    });

    it("rejects invalid currency", () => {
      const invalidLine = {
        description: "Test item",
        amount: 100,
        currency: "INVALID",
      };

      const result = InvoiceLineSchema.safeParse(invalidLine);
      expect(result.success).toBe(false);
    });

    it("accepts all valid currencies", () => {
      CURRENCIES.forEach((currency) => {
        const validLine = {
          description: "Test item",
          amount: 100,
          currency,
        };

        const result = InvoiceLineSchema.safeParse(validLine);
        expect(result.success).toBe(true);
      });
    });
  });

  describe("InvoiceSchema", () => {
    const validLineItem = {
      description: "Test item",
      amount: 100,
      currency: "USD" as const,
    };

    it("validates a correct invoice", () => {
      const validInvoice = {
        date: new Date("2024-01-01"),
        currency: "NZD" as const,
        lines: [validLineItem],
      };

      const result = InvoiceSchema.safeParse(validInvoice);
      expect(result.success).toBe(true);
    });

    it("rejects date before 1999-01-04", () => {
      const invalidInvoice = {
        date: new Date("1999-01-03"),
        currency: "NZD" as const,
        lines: [validLineItem],
      };

      const result = InvoiceSchema.safeParse(invalidInvoice);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe(
          "Date must be after 1999-01-04"
        );
      }
    });

    it("accepts date exactly on 1999-01-04", () => {
      const validInvoice = {
        date: new Date("1999-01-04"),
        currency: "NZD" as const,
        lines: [validLineItem],
      };

      const result = InvoiceSchema.safeParse(validInvoice);
      expect(result.success).toBe(true);
    });

    it("rejects empty lines array", () => {
      const invalidInvoice = {
        date: new Date("2024-01-01"),
        currency: "NZD" as const,
        lines: [],
      };

      const result = InvoiceSchema.safeParse(invalidInvoice);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe(
          "At least one line item is required"
        );
      }
    });

    it("rejects invalid base currency", () => {
      const invalidInvoice = {
        date: new Date("2024-01-01"),
        currency: "INVALID",
        lines: [validLineItem],
      };

      const result = InvoiceSchema.safeParse(invalidInvoice);
      expect(result.success).toBe(false);
    });

    it("validates multiple line items", () => {
      const validInvoice = {
        date: new Date("2024-01-01"),
        currency: "EUR" as const,
        lines: [
          { description: "Item 1", amount: 50, currency: "USD" as const },
          { description: "Item 2", amount: 75.25, currency: "GBP" as const },
          { description: "Item 3", amount: 100, currency: "JPY" as const },
        ],
      };

      const result = InvoiceSchema.safeParse(validInvoice);
      expect(result.success).toBe(true);
    });

    it("rejects if any line item is invalid", () => {
      const invalidInvoice = {
        date: new Date("2024-01-01"),
        currency: "EUR" as const,
        lines: [
          { description: "Valid item", amount: 50, currency: "USD" as const },
          { description: "", amount: 75, currency: "GBP" as const }, // Invalid: empty description
        ],
      };

      const result = InvoiceSchema.safeParse(invalidInvoice);
      expect(result.success).toBe(false);
    });
  });
});
