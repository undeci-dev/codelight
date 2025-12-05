// 파일 업로드 UI 상태 관리용 (로컬 상태)
export interface FileUploadState {
  id: string;
  file: File;
  previewUrl: string;
  fileName: string;
  fileSize: number;
}

// 서버에서 파일 응답 시 사용
export interface FileResponse {
  fileId: number;
  fileUrl: string;
  s3Key: string;
  fileName: string;
  fileSize: number;
  displayOrder: number;
}

// 수정 시 기존 파일과 새 파일을 함께 관리하기 위한 타입
export interface EditableFileState {
  type: 'existing' | 'new';
  id: string; // existing: `existing-${fileId}`, new: `new-${timestamp}-${index}`
  previewUrl: string;
  fileName: string;
  fileSize: number;
  // 기존 파일인 경우
  fileId?: number;
  // 새 파일인 경우
  file?: File;
}
