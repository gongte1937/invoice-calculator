import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  CURRENCIES,
  DEFAULT_BASE_CURRENCY,
  DEFAULT_LINE_CURRENCY,
} from "../../app/constants";

// Create a proper tuple for Zod enum
const CURRENCIES_ENUM = CURRENCIES as readonly [string, ...string[]];

// Zod validation schema
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

export type InvoiceFormData = z.infer<typeof InvoiceSchema>;

interface InvoiceFormWrapperProps {
  children: (props: {
    control: any;
    handleSubmit: any;
    errors: any;
    onSubmit: (data: InvoiceFormData) => void;
  }) => React.ReactNode;
  onSubmit?: (data: InvoiceFormData) => void;
  defaultValues?: Partial<InvoiceFormData>;
}

export default function InvoiceFormWrapper({
  children,
  onSubmit,
  defaultValues,
}: InvoiceFormWrapperProps) {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<InvoiceFormData>({
    resolver: zodResolver(InvoiceSchema),
    defaultValues: {
      date: new Date(),
      currency: DEFAULT_BASE_CURRENCY,
      lines: [
        { description: "", amount: null, currency: DEFAULT_LINE_CURRENCY },
      ],
      ...defaultValues,
    },
  });

  const handleFormSubmit = (data: InvoiceFormData) => {
    if (onSubmit) {
      onSubmit(data);
    } else {
      console.log("Form submit:", data);
    }
  };

  return (
    <>
      {children({
        control,
        handleSubmit: handleSubmit(handleFormSubmit),
        errors,
        onSubmit: handleFormSubmit,
      })}
    </>
  );
}
