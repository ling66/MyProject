//
//  ItemDataSource.m
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "ItemDataSource.h"
#import "Item.h"
#import "TextItem.h"
#import "ButtonItem.h"
#import "ImagesItem.h"

@implementation ItemDataSource
@synthesize items = _items;

-(id)init
{
    self = [super init];
    if(self)
    {
        self.items = [[NSMutableArray alloc] init];
        
        TextItem *text1 = [[TextItem alloc] init];
        text1.bgColor = [UIColor blueColor];
        text1.text = @"性能不是唯一的，实际体验才是王道：\n虽然横评中那几款高性能的SSD跑分表现相当出色，但是细心的网友会发现它们实际应用领先的幅度远没有跑分大。要是大众用户的话，日常普通办公娱乐，主流的SandForce主控的产品也同样让你效率倍增。而且横评过程中对于SSD的稳定性，可靠性，寿命等问题因条件、时间有限并无深入研究。性能是一个重要的参考标准，但盲目追求也不科学，如果用户在使用过程常遇故障，那损失难以估量。\n跑分性能并不是唯一的参考标准，用户实际运用中的体验才是关键，平时大家可以多关注下PConline的SSD频道及SSD论坛 晒晒使用SSD的体验！还有就是不得不提下质保的问题，在产品同质化严重的今天，购买SSD时要留心下质保年限与售后服务，这也是。\n写在最后：\n一款SSD性能强弱考究的是主控，闪存及固件算法。SandForce主控的发展至今似乎遇到不少瓶颈，被竞争对手所超越，但其成本低，提供全套解决方案又让众多厂商依依不舍，Intel虽然同样采用SandForce的主控，但独家开发的固件算法让它的产品不至于被对手抛开很远。接下来，如何提升SandForce主控综合读写能力成为每个厂商应该思考的问题。\n真心希望SF主控方案的厂商能再接再厉，推出更多让人可信赖、性能实事求是、性价比高的产品，一家独大不是我们的愿望，百家争鸣才是消费者的福音\n\n性能不是唯一的，实际体验才是王道：\n虽然横评中那几款高性能的SSD跑分表现相当出色，但是细心的网友会发现它们实际应用领先的幅度远没有跑分大。要是大众用户的话，日常普通办公娱乐，主流的SandForce主控的产品也同样让你效率倍增。而且横评过程中对于SSD的稳定性，可靠性，寿命等问题因条件、时间有限并无深入研究。性能是一个重要的参考标准，但盲目追求也不科学，如果用户在使用过程常遇故障，那损失难以估量。\n跑分性能并不是唯一的参考标准，用户实际运用中的体验才是关键，平时大家可以多关注下PConline的SSD频道及SSD论坛 晒晒使用SSD的体验！还有就是不得不提下质保的问题，在产品同质化严重的今天，购买SSD时要留心下质保年限与售后服务，这也是。\n写在最后：\n一款SSD性能强弱考究的是主控，闪存及固件算法。SandForce主控的发展至今似乎遇到不少瓶颈，被竞争对手所超越，但其成本低，提供全套解决方案又让众多厂商依依不舍，Intel虽然同样采用SandForce的主控，但独家开发的固件算法让它的产品不至于被对手抛开很远。接下来，如何提升SandForce主控综合读写能力成为每个厂商应该思考的问题。\n真心希望SF主控方案的厂商能再接再厉，推出更多让人可信赖、性能实事求是、性价比高的产品，一家独大不是我们的愿望，百家争鸣才是消费者的福音";
        
        TextItem *text2 = [[TextItem alloc] init];
        text2.bgColor = [UIColor grayColor];
        text2.text = @"aaaaaaaaa\nbbbbbbbbbb\ncccccccccccc\nddddd\neeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaa\nbbbbbbbbbb\ncccccccccccc\nddddd\neeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaa\nbbbbbbbbbb\ncccccccccccc\nddddd\neeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaa\nbbbbbbbbbb\ncccccccccccc\nddddd\neeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaabbbbbbbbbbccccccccccccdddddeeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaa\nbbbbbbbbbb\ncccccccccccc\nddddd\neeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaa\nbbbbbbbbbb\ncccccccccccc\nddddd\neeeeeeeeeeeeeeeeeeeeeeeaaaaaaaaa\nbbbbbbbbbb\ncccccccccccc\nddddd\neeeeeeeeeeeeeeeeeeeeeee";
        
        TextItem *text3 = [[TextItem alloc] init];
        text3.bgColor = [UIColor greenColor];
        text3.text = @"性能不是唯一的，实际体验才是王道：\n虽然横评中那几款高性能的SSD跑分表现相当出色，但是细心的网友会发现它们实际应用领先的幅度远没有跑分大。要是大众用户的话，日常普通办公娱乐，主流的SandForce主控的产品也同样让你效率倍增。而且横评过程中对于SSD的稳定性，可靠性，寿命等问题因条件、时间有限并无深入研究。性能是一个重要的参考标准，但盲目追求也不科学，如果用户在使用过程常遇故障，那损失难以估量。\n跑分性能并不是唯一的参考标准，用户实际运用中的体验才是关键，平时大家可以多关注下PConline的SSD频道及SSD论坛 晒晒使用SSD的体验！还有就是不得不提下质保的问题，在产品同质化严重的今天，购买SSD时要留心下质保年限与售后服务，这也是。";
        
        ButtonItem *button1 = [[ButtonItem alloc] init];
        button1.text = @"Press Me1";
        
        ButtonItem *button2 = [[ButtonItem alloc] init];
        button2.text = @"Press Me2";
        
        ImagesItem *imagesItem = [[ImagesItem alloc] init];
        [imagesItem.imageArray addObject:@"image_2.JPG"];
        [imagesItem.imageArray addObject:@"image_1.JPG"];
        [imagesItem.imageArray addObject:@"image_2.JPG"];
        [imagesItem.imageArray addObject:@"image_1.JPG"];
        [imagesItem.imageArray addObject:@"image_2.JPG"];
        [imagesItem.imageArray addObject:@"image_1.JPG"];
        [imagesItem.imageArray addObject:@"image_2.JPG"];
        [imagesItem.imageArray addObject:@"image_1.JPG"];
        [imagesItem.imageArray addObject:@"image_2.JPG"];
        [imagesItem.imageArray addObject:@"image_1.JPG"];
        [imagesItem.imageArray addObject:@"image_2.JPG"];
        
        [self.items addObject:imagesItem];
        [self.items addObject:text1];
        [self.items addObject:text2];
        [self.items addObject:text3];
        [self.items addObject:button1];
        [self.items addObject:button2];
        
    }
    return self;
}

@end
