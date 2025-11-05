import React, { useState, useCallback } from 'react';

import {
  VALIDATION_ERRORS,
  ValidationError,
} from '@/constants/messages/validationErrors';

export type ValidationResult = {
  isValid: boolean;
  errorType: ValidationError;
};

export type ValidateFunction = (value: string) => ValidationResult;

interface UseValidateInputProps {
  initialValue?: string;
  validates: ValidateFunction[];
}

const useValidateInput = ({
  initialValue = '',
  validates,
}: UseValidateInputProps) => {
  const [value, setValue] = useState(initialValue);
  const [error, setError] = useState<ValidationError | null>(null);
  const [touched, setTouched] = useState(false);

  const validate = useCallback(
    (inputValue: string): ValidationError | null => {
      for (const validateFn of validates) {
        const result = validateFn(inputValue);
        if (!result.isValid) {
          return result.errorType;
        }
      }
      return null;
    },
    [validates]
  );

  const onChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      const newValue = event.target.value;
      setValue(newValue);

      const validationError = validate(newValue);
      setError(validationError);
    },
    [validate]
  );

  const onBlur = useCallback(() => {
    setTouched(true);
  }, []);

  const getErrorMessage = () => (error ? VALIDATION_ERRORS[error] : '');

  const isValidated = !error && value.length > 0;

  const setInputValue = useCallback(
    (newValue: string) => {
      setValue(newValue);
      const validationError = validate(newValue);
      setError(validationError);
    },
    [validate]
  );

  const reset = useCallback(() => {
    setValue(initialValue);
    setError(null);
    setTouched(false);
  }, [initialValue]);

  return {
    value,
    onChange,
    onBlur,
    getErrorMessage,
    isValidated,
    touched,
    setValue: setInputValue,
    reset,
  };
};

export default useValidateInput;
