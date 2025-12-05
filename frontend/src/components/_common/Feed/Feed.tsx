import FeedWrapper from './FeedWrapper';
import FeedHeader from './FeedHeader';
import FeedComposer from './FeedComposer';
import FeedList from './FeedList';
import FeedPost from './FeedPost';
import FeedEmpty from './FeedEmpty';
import FeedLinkPreview from './FeedLinkPreview';
import FeedLinkPreviewList from './FeedLinkPreviewList';
import FeedPoll from './FeedPoll';
import FeedPollPreview from './FeedPollPreview';
import FeedComposerToolbar from './FeedComposerToolbar';
import FeedComments from './FeedComments';
import FeedCommentItem from './FeedCommentItem';
import FeedCommentInput from './FeedCommentInput';

const Feed = Object.assign(FeedWrapper, {
  Header: FeedHeader,
  Composer: FeedComposer,
  List: FeedList,
  Post: FeedPost,
  Empty: FeedEmpty,
  LinkPreview: FeedLinkPreview,
  LinkPreviewList: FeedLinkPreviewList,
  Poll: FeedPoll,
  PollPreview: FeedPollPreview,
  ComposerToolbar: FeedComposerToolbar,
  Comments: FeedComments,
  CommentItem: FeedCommentItem,
  CommentInput: FeedCommentInput,
});

export default Feed;
