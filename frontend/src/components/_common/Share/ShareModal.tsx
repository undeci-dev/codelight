import { useState, useEffect, useRef } from 'react';
import { X, Link2, Check } from 'lucide-react';
import Text from '@/components/_common/Text/Text';
import {
  ShareData,
  copyToClipboard,
  canUseNativeShare,
  nativeShare,
  shareToKakao,
  shareToLinkedIn,
  isKakaoInitialized,
} from '@/utils/share';

interface ShareModalProps {
  isOpen: boolean;
  onClose: () => void;
  shareData: ShareData & { imageUrl?: string };
}

const ShareModal = ({ isOpen, onClose, shareData }: ShareModalProps) => {
  const [copied, setCopied] = useState(false);
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (copied) {
      const timer = setTimeout(() => setCopied(false), 2000);
      return () => clearTimeout(timer);
    }
  }, [copied]);

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };

    const handleClickOutside = (e: MouseEvent) => {
      if (modalRef.current && !modalRef.current.contains(e.target as Node)) {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener('keydown', handleEscape);
      document.addEventListener('mousedown', handleClickOutside);
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('keydown', handleEscape);
      document.removeEventListener('mousedown', handleClickOutside);
      document.body.style.overflow = '';
    };
  }, [isOpen, onClose]);

  const handleCopy = async () => {
    const success = await copyToClipboard(shareData.url);
    if (success) {
      setCopied(true);
    }
  };

  const handleNativeShare = async () => {
    const success = await nativeShare(shareData);
    if (success) {
      onClose();
    }
  };

  const handleKakaoShare = () => {
    const success = shareToKakao(shareData);
    if (success) {
      onClose();
    }
  };

  if (!isOpen) return null;

  return (
    <div className='fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50'>
      <div
        ref={modalRef}
        className='mx-4 w-full max-w-sm rounded-lg bg-white p-6'
      >
        <div className='mb-4 flex items-center justify-between'>
          <Text typography='h3' color='gray900'>
            공유하기
          </Text>
          <button
            onClick={onClose}
            className='rounded-full p-1 transition-colors hover:bg-gray-100'
          >
            <X size={20} className='text-gray-500' />
          </button>
        </div>

        {/* 공유 플랫폼 버튼 */}
        <div className='mb-6 flex justify-center gap-4'>
          {/* 카카오톡 */}
          {isKakaoInitialized() && (
            <button
              onClick={handleKakaoShare}
              className='flex flex-col items-center gap-2'
            >
              <div className='flex h-12 w-12 items-center justify-center rounded-full bg-[#FEE500] transition-transform hover:scale-105'>
                <svg className='h-6 w-6' viewBox='0 0 24 24' fill='#000000'>
                  <path d='M12 3C6.48 3 2 6.58 2 11c0 2.83 1.88 5.32 4.71 6.72-.16.57-.58 2.07-.67 2.39-.11.4.14.39.3.29.12-.08 1.94-1.32 2.73-1.86.62.09 1.26.14 1.93.14 5.52 0 10-3.58 10-8s-4.48-8-10-8z' />
                </svg>
              </div>
              <Text typography='caption' color='gray600'>
                카카오톡
              </Text>
            </button>
          )}

          {/* LinkedIn */}
          <button
            onClick={() => shareToLinkedIn(shareData)}
            className='flex flex-col items-center gap-2'
          >
            <div className='flex h-12 w-12 items-center justify-center rounded-full bg-[#0A66C2] transition-transform hover:scale-105'>
              <svg
                className='h-5 w-5 text-white'
                viewBox='0 0 24 24'
                fill='currentColor'
              >
                <path d='M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z' />
              </svg>
            </div>
            <Text typography='caption' color='gray600'>
              LinkedIn
            </Text>
          </button>

          {/* 기본 공유 (모바일) */}
          {canUseNativeShare() && (
            <button
              onClick={handleNativeShare}
              className='flex flex-col items-center gap-2'
            >
              <div className='flex h-12 w-12 items-center justify-center rounded-full bg-purple-600 transition-transform hover:scale-105'>
                <svg
                  className='h-5 w-5 text-white'
                  viewBox='0 0 24 24'
                  fill='none'
                  stroke='currentColor'
                  strokeWidth='2'
                >
                  <path d='M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8' />
                  <polyline points='16 6 12 2 8 6' />
                  <line x1='12' y1='2' x2='12' y2='15' />
                </svg>
              </div>
              <Text typography='caption' color='gray600'>
                더보기
              </Text>
            </button>
          )}
        </div>

        {/* 링크 복사 */}
        <div className='rounded-lg border border-gray-200 bg-gray-50 p-3'>
          <Text typography='caption' color='gray500' className='mb-2 block'>
            링크 복사
          </Text>
          <div className='flex items-center gap-2'>
            <input
              type='text'
              value={shareData.url}
              readOnly
              className='flex-1 truncate rounded border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700'
            />
            <button
              onClick={handleCopy}
              className={`flex items-center gap-1 rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
                copied
                  ? 'bg-green-100 text-green-700'
                  : 'bg-purple-600 text-white hover:bg-purple-700'
              }`}
            >
              {copied ? (
                <>
                  <Check size={16} />
                  복사됨
                </>
              ) : (
                <>
                  <Link2 size={16} />
                  복사
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShareModal;
