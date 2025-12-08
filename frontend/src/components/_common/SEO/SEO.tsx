import { Helmet } from 'react-helmet-async';
import codelightLogo from '@/assets/images/common/codelight-logo.svg';

interface SEOProps {
  title?: string;
  description?: string;
  image?: string;
  url?: string;
  type?: 'website' | 'article';
  author?: string;
}

const DEFAULT_TITLE = 'CodeLight';
const DEFAULT_DESCRIPTION = 'CodeLight - 개발자 커뮤니티';

const SEO = ({
  title,
  description = DEFAULT_DESCRIPTION,
  image,
  url,
  type = 'website',
  author,
}: SEOProps) => {
  const siteTitle = title ? `${title} | CodeLight` : DEFAULT_TITLE;
  const currentUrl =
    url || (typeof window !== 'undefined' ? window.location.href : '');
  const imageUrl = image
    ? image.startsWith('http')
      ? image
      : `${window.location.origin}${image}`
    : `${window.location.origin}${codelightLogo}`;

  return (
    <Helmet>
      {/* 기본 메타 태그 */}
      <title>{siteTitle}</title>
      <meta name='description' content={description} />

      {/* Open Graph 태그 */}
      <meta property='og:title' content={siteTitle} />
      <meta property='og:description' content={description} />
      <meta property='og:image' content={imageUrl} />
      <meta property='og:url' content={currentUrl} />
      <meta property='og:type' content={type} />
      <meta property='og:site_name' content='CodeLight' />

      {/* 추가 메타 태그 */}
      {author && <meta name='author' content={author} />}
      {type === 'article' && (
        <meta property='og:article:author' content={author} />
      )}
    </Helmet>
  );
};

export default SEO;
