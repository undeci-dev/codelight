import { useRef, ChangeEvent } from 'react';
import { X, Upload } from 'lucide-react';
import type { FileUploadState } from '@/types/file';
import Button from '@/components/_common/Button/Button';

interface FileUploadProps {
  files: FileUploadState[];
  onFilesChange: (files: FileUploadState[]) => void;
  maxFiles?: number;
  maxSize?: number; // in MB
}

const FileUpload = ({
  files,
  onFilesChange,
  maxFiles = 4,
}: FileUploadProps) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = (e: ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(e.target.files || []);

    if (files.length + selectedFiles.length > maxFiles) {
      alert(`최대 ${maxFiles}개의 파일만 업로드할 수 있습니다.`);
      return;
    }

    const newFiles: FileUploadState[] = selectedFiles.map((file, index) => ({
      id: `${Date.now()}-${index}-${file.name}`,
      file,
      previewUrl: URL.createObjectURL(file),
      fileName: file.name,
      fileSize: file.size,
    }));

    onFilesChange([...files, ...newFiles]);

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleRemove = (id: string) => {
    const fileToRemove = files.find((f) => f.id === id);
    if (fileToRemove?.previewUrl) {
      URL.revokeObjectURL(fileToRemove.previewUrl);
    }
    onFilesChange(files.filter((f) => f.id !== id));
  };

  return (
    <div className='space-y-3'>
      <input
        ref={fileInputRef}
        type='file'
        multiple
        accept='image/*'
        onChange={handleFileSelect}
        className='hidden'
      />

      {files.length < maxFiles && (
        <Button
          label='파일 선택'
          variant='secondary'
          size='sm'
          onClick={() => fileInputRef.current?.click()}
          Icon={Upload}
        />
      )}

      {files.length > 0 && (
        <div className='grid grid-cols-2 gap-3'>
          {files.map((file) => (
            <div
              key={file.id}
              className='relative rounded-lg border border-gray-200 p-3'
            >
              <button
                onClick={() => handleRemove(file.id)}
                className='absolute right-2 top-2 z-10 rounded-full bg-gray-900 bg-opacity-70 p-1 text-white hover:bg-opacity-90'
              >
                <X size={16} />
              </button>

              <div className='relative'>
                <img
                  src={file.previewUrl}
                  alt={file.fileName}
                  className='h-32 w-full rounded object-cover'
                />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FileUpload;
